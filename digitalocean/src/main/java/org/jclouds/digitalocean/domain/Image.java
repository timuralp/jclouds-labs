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
package org.jclouds.digitalocean.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.List;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

/**
 * An Image.
 */
public class Image {
   private final int id;
   private final String name;
   private final OperatingSystem os;
   private final boolean publicImage;
   private final String slug;
   @Named("regions")
   private final List<Integer> regionIds;
   @Named("region_slugs")
   private final List<String> regionSlugs;

   @ConstructorProperties({ "id", "name", "distribution", "public", "slug", "regions", "region_slugs" })
   public Image(int id, String name, String distribution, boolean publicImage, @Nullable String slug,
         List<Integer> regionIds, List<String> regionSlugs) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.os = OperatingSystem.builder().from(name, checkNotNull(distribution, "distribution")).build();
      this.publicImage = publicImage;
      this.slug = slug;
      this.regionIds = ImmutableList.copyOf(checkNotNull(regionIds, "regionIds"));
      this.regionSlugs = ImmutableList.copyOf(checkNotNull(regionSlugs, "regionSlugs"));
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public OperatingSystem getOs() {
      return os;
   }

   public boolean isPublicImage() {
      return publicImage;
   }

   public String getSlug() {
      return slug;
   }

   public List<Integer> getRegionIds() {
      return regionIds;
   }

   public List<String> getRegionSlugs() {
      return regionSlugs;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + (os == null ? 0 : os.hashCode());
      result = prime * result + (publicImage ? 1231 : 1237);
      result = prime * result + (slug == null ? 0 : slug.hashCode());
      result = prime * result + (regionIds == null ? 0 : regionIds.hashCode());
      result = prime * result + (regionSlugs == null ? 0 : regionSlugs.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Image other = (Image) obj;
      if (id != other.id) {
         return false;
      }
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      if (os == null) {
         if (other.os != null) {
            return false;
         }
      } else if (!os.equals(other.os)) {
         return false;
      }
      if (publicImage != other.publicImage) {
         return false;
      }
      if (slug == null) {
         if (other.slug != null) {
            return false;
         }
      } else if (!slug.equals(other.slug)) {
         return false;
      }
      if (regionIds == null) {
         if (other.regionIds != null) {
            return false;
         }
      } else if (!regionIds.equals(other.regionIds)) {
         return false;
      }
      if (regionSlugs == null) {
         if (other.regionSlugs != null) {
            return false;
         }
      } else if (!regionSlugs.equals(other.regionSlugs)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "Image [id=" + id + ", name=" + name + ", os=" + os + ", publicImage=" + publicImage + ", slug=" + slug
            + ", regionIds=" + regionIds + ", regionSlugs=" + regionSlugs + "]";
   }

}
