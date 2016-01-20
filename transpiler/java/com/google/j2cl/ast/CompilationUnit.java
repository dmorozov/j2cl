/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.j2cl.ast;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.j2cl.ast.processors.Context;
import com.google.j2cl.ast.processors.Visitable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A model class that represents a Java Compilation Unit.
 */
@Visitable
@Context
public class CompilationUnit extends Node {
  private String filePath;
  private String packageName;
  @Visitable List<JavaType> types = new ArrayList<>();

  public CompilationUnit(String filePath, String packageName) {
    Preconditions.checkNotNull(filePath);
    Preconditions.checkNotNull(packageName);
    this.filePath = filePath;
    this.packageName = packageName;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getDirectoryPath() {
    Preconditions.checkNotNull(filePath);
    if (!filePath.contains(File.separator)) {
      return "";
    }
    return filePath.substring(0, filePath.lastIndexOf(File.separator));
  }

  public String getFileName() {
    Preconditions.checkNotNull(filePath);
    String[] pathComponents = filePath.split(File.separator);
    return pathComponents[pathComponents.length - 1];
  }

  public String getPackageName() {
    return packageName;
  }

  public void addType(JavaType type) {
    this.types.add(Preconditions.checkNotNull(type));
  }

  public List<JavaType> getTypes() {
    return types;
  }

  public JavaType getType(final TypeDescriptor typeDescriptor) {
    return FluentIterable.from(types)
        .firstMatch(
            new Predicate<JavaType>() {
              @Override
              public boolean apply(JavaType javaType) {
                return javaType.getDescriptor().getRawTypeDescriptor()
                    == typeDescriptor.getRawTypeDescriptor();
              }
            })
        .orNull();
  }

  public String getName() {
    int endIndex = filePath.lastIndexOf(".java");
    Preconditions.checkState(endIndex != -1);

    return filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1, endIndex);
  }

  @Override
  public Node accept(Processor processor) {
    return Visitor_CompilationUnit.visit(processor, this);
  }
}
