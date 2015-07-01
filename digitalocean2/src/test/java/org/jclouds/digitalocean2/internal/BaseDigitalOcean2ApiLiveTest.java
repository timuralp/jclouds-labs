/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.digitalocean2.internal;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.testng.Assert.assertEquals;
import static org.testng.util.Strings.isNullOrEmpty;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Image;
import org.jclouds.digitalocean2.domain.Region;
import org.jclouds.digitalocean2.domain.Size;

import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class BaseDigitalOcean2ApiLiveTest extends BaseApiLiveTest<DigitalOcean2Api> {

   protected Predicate<Integer> actionCompleted;
   protected Predicate<AtomicReference<NodeMetadata>> nodeRunning;
   protected Predicate<Integer> nodeTerminated;
   protected Predicate<Integer> dropletOff;

   public BaseDigitalOcean2ApiLiveTest() {
      provider = "digitalocean2";
   }

   @Override protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(ComputeServiceProperties.POLL_INITIAL_PERIOD, 1000);
      props.put(ComputeServiceProperties.POLL_MAX_PERIOD, 10000);
      return props;
   }

   @Override protected DigitalOcean2Api create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      actionCompleted = injector.getInstance(Key.get(new TypeLiteral<Predicate<Integer>>(){}));
      nodeTerminated = injector.getInstance(Key.get(new TypeLiteral<Predicate<Integer>>(){},
            Names.named(TIMEOUT_NODE_TERMINATED)));
      return injector.getInstance(DigitalOcean2Api.class);
   }

   protected void assertActionCompleted(int actionId) {
      checkState(actionCompleted.apply(actionId), "Timeout waiting for action: %s", actionId);
      Action action = api.actionApi().get(actionId);
      assertEquals(action.status(), Action.Status.COMPLETED);
   }

   protected void assertNodeTerminated(int dropletId) {
      assertEquals(nodeTerminated.apply(dropletId), true, String.format("Timeout waiting for dropletId: %s", dropletId));
   }
   
   protected Region firstAvailableRegion() {
      return api.regionApi().list().concat().firstMatch(new Predicate<Region>() {
         @Override
         public boolean apply(Region input) {
            return input.available();
         }
      }).get();
   }
   
   protected Size cheapestSizeInRegion(final Region region) {
      return sizesByPrice().min(api.sizeApi().list().concat().filter(new Predicate<Size>() {
         @Override
         public boolean apply(Size input) {
            return input.available() && input.regions().contains(region.slug());
         }
      }));
   }
   
   protected Image ubuntuImageInRegion(final Region region) {
      return api.imageApi().list().concat().firstMatch(new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            return "Ubuntu".equalsIgnoreCase(input.distribution()) && !isNullOrEmpty(input.slug())
                  && input.regions().contains(region.slug());
         }
      }).get();
   }
   
   protected static Ordering<Size> sizesByPrice() {
      return new Ordering<Size>() {
         @Override
         public int compare(Size left, Size right) {
            return ComparisonChain.start()
                  .compare(left.priceHourly(), right.priceHourly())
                  .compare(left.priceMonthly(), right.priceMonthly())
                  .result();
         }
      };
   }
}