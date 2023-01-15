/* Copyright (C) 2013-2022 TU Dortmund
 * This file is part of AutomataLib, http://www.automatalib.net/.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.automatalib.commons.smartcollections;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;

/**
 * An immutable, reflexive {@link Map} view for a given set of elements. This map is backed by the given set elements,
 * i.e. changes to the passed sets are propagated to this map.
 *
 * @param <T>
 *         element type
 *
 * @author frohme
 */
public class ReflexiveMapView<T> extends AbstractMap<T, T> {

    private final Set<T> domain;

    public ReflexiveMapView(Set<T> domain) {
        this.domain = Collections.unmodifiableSet(domain);
    }

    @Override
    public Set<Entry<T, T>> entrySet() {
        return new EntrySet();
    }

    @Override
    public boolean containsValue(Object value) {
        return containsKey(value);
    }

    @Override
    @SuppressWarnings("contracts.conditional.postcondition.not.satisfied") // condition is satisfied
    public boolean containsKey(Object key) {
        return this.domain.contains(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(Object key) {
        return this.domain.contains(key) ? (T) key : null;
    }

    @Override
    public Set<T> keySet() {
        return this.domain;
    }

    @Override
    public Set<T> values() {
        return (Set<T>) this.domain;
    }

    private class EntrySet extends AbstractSet<Entry<T, T>> {

        @Override
        public Iterator<Entry<T, T>> iterator() {
            return new Iter();
        }

        @Override
        public int size() {
            return domain.size();
        }
    }

    private class Iter implements Iterator<Entry<T, T>> {

        final Iterator<T> iter = domain.iterator();

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public Entry<T, T> next() {
            final T next = iter.next();
            return new SimpleImmutableEntry<>(next, next);
        }
    }
}
