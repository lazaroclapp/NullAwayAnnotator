/*
 * MIT License
 *
 * Copyright (c) 2020 Nima Karimipour
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package edu.ucr.cs.riple.core.metadata.method;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MethodNode {
  public List<Integer> children;
  public Integer parent;
  public Integer id;
  public String method;
  public String clazz;
  public int size;
  public boolean[] annotFlags;
  public boolean hasNullableAnnotation;

  public MethodNode(
      int id,
      String clazz,
      String method,
      List<Integer> children,
      boolean[] annotFlags,
      int parent,
      boolean hasNullableAnnotation) {
    this.id = id;
    this.clazz = clazz;
    this.method = method;
    this.children = children;
    this.annotFlags = annotFlags;
    this.parent = parent;
    this.hasNullableAnnotation = hasNullableAnnotation;
  }

  public MethodNode() {}

  void fillInformation(
      Integer id,
      String clazz,
      String method,
      Integer parent,
      int size,
      boolean[] annotFlags,
      boolean hasNullableAnnotation) {
    this.parent = parent;
    this.id = id;
    this.method = method;
    this.clazz = clazz;
    this.size = size;
    this.annotFlags = annotFlags;
    this.hasNullableAnnotation = hasNullableAnnotation;
  }

  void addChild(Integer id) {
    if (children == null) {
      children = new ArrayList<>();
    }
    children.add(id);
  }

  @Override
  public String toString() {
    return "parent=" + parent + ", id=" + id + ", method='" + method + '\'' + ", clazz='" + clazz;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MethodNode)) return false;
    MethodNode that = (MethodNode) o;
    return Objects.equals(children, that.children)
        && Objects.equals(parent, that.parent)
        && Objects.equals(id, that.id)
        && Objects.equals(method, that.method)
        && Objects.equals(clazz, that.clazz);
  }

  @Override
  public int hashCode() {
    return Objects.hash(method, clazz);
  }
}
