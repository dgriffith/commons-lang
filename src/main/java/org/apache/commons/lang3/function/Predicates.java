/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.lang3.function;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Factory for {@link Predicate}.
 *
 * @since 3.18.0
 */
public class Predicates {

    private static final Predicate<?> TRUE = t -> true;
    private static final Predicate<?> FALSE = t -> false;

    /**
     * Gets the Predicate singleton that always returns false.
     *
     * @param <T> the type of the input to the predicate.
     * @return the Predicate singleton.
     */
    @SuppressWarnings("unchecked")
    // method name cannot be "false".
    public static <T> Predicate<T> falsePredicate() {
        return (Predicate<T>) FALSE;
    }

    /**
     * Gets the Predicate singleton that always returns true.
     *
     * @param <T> the type of the input to the predicate.
     * @return the Predicate singleton.
     */
    @SuppressWarnings("unchecked")
    // method name cannot be "true".
    public static <T> Predicate<T> truePredicate() {
        return (Predicate<T>) TRUE;
    }

    /**
     * Returns a predicate that performs a logical AND operation on multiple predicates.
     * The returned predicate evaluates to {@code true} if all provided predicates 
     * evaluate to {@code true}. Short-circuit evaluation is performed, meaning 
     * evaluation stops at the first predicate that returns {@code false}.
     * 
     * <p>Null predicates are handled based on the {@code nullDefault} parameter:
     * if {@code true}, null predicates are treated as always returning {@code true};
     * if {@code false}, null predicates are treated as always returning {@code false}.</p>
     *
     * @param <T> the type of the input to the predicates
     * @param nullDefault the default value for null predicates
     * @param predicates the predicates to AND together
     * @return a predicate that represents the logical AND of the predicates
     * @throws IllegalArgumentException if the predicates array is null or empty
     * @since 3.18.0
     */
    @SafeVarargs
    public static <T> Predicate<T> and(final boolean nullDefault, final Predicate<T>... predicates) {
        Objects.requireNonNull(predicates, "Predicates array cannot be null");
        if (predicates.length == 0) {
            throw new IllegalArgumentException("At least one predicate must be provided");
        }
        
        return input -> {
            for (final Predicate<T> predicate : predicates) {
                final boolean result = predicate == null ? nullDefault : predicate.test(input);
                if (!result) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Returns a predicate that performs a logical AND operation on multiple predicates.
     * This is equivalent to calling {@code and(false, predicates)}, meaning null
     * predicates are treated as always returning {@code false}.
     *
     * @param <T> the type of the input to the predicates
     * @param predicates the predicates to AND together
     * @return a predicate that represents the logical AND of the predicates
     * @throws IllegalArgumentException if the predicates array is null or empty
     * @since 3.18.0
     */
    @SafeVarargs
    public static <T> Predicate<T> and(final Predicate<T>... predicates) {
        return and(false, predicates);
    }

    /**
     * Returns a predicate that performs a logical OR operation on multiple predicates.
     * The returned predicate evaluates to {@code true} if any provided predicate 
     * evaluates to {@code true}. Short-circuit evaluation is performed, meaning 
     * evaluation stops at the first predicate that returns {@code true}.
     * 
     * <p>Null predicates are handled based on the {@code nullDefault} parameter:
     * if {@code true}, null predicates are treated as always returning {@code true};
     * if {@code false}, null predicates are treated as always returning {@code false}.</p>
     *
     * @param <T> the type of the input to the predicates
     * @param nullDefault the default value for null predicates
     * @param predicates the predicates to OR together
     * @return a predicate that represents the logical OR of the predicates
     * @throws IllegalArgumentException if the predicates array is null or empty
     * @since 3.18.0
     */
    @SafeVarargs
    public static <T> Predicate<T> or(final boolean nullDefault, final Predicate<T>... predicates) {
        Objects.requireNonNull(predicates, "Predicates array cannot be null");
        if (predicates.length == 0) {
            throw new IllegalArgumentException("At least one predicate must be provided");
        }
        
        return input -> {
            for (final Predicate<T> predicate : predicates) {
                final boolean result = predicate == null ? nullDefault : predicate.test(input);
                if (result) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Returns a predicate that performs a logical OR operation on multiple predicates.
     * This is equivalent to calling {@code or(false, predicates)}, meaning null
     * predicates are treated as always returning {@code false}.
     *
     * @param <T> the type of the input to the predicates
     * @param predicates the predicates to OR together
     * @return a predicate that represents the logical OR of the predicates
     * @throws IllegalArgumentException if the predicates array is null or empty
     * @since 3.18.0
     */
    @SafeVarargs
    public static <T> Predicate<T> or(final Predicate<T>... predicates) {
        return or(false, predicates);
    }

    /**
     * Returns a predicate that performs a logical NOT operation on the provided predicate.
     * The returned predicate evaluates to {@code true} if the provided predicate 
     * evaluates to {@code false}, and vice versa.
     * 
     * <p>If the predicate is null, the behavior is determined by the {@code nullDefault} 
     * parameter: if {@code true}, a null predicate is treated as always returning 
     * {@code true} (so NOT returns {@code false}); if {@code false}, a null predicate 
     * is treated as always returning {@code false} (so NOT returns {@code true}).</p>
     *
     * @param <T> the type of the input to the predicate
     * @param predicate the predicate to negate
     * @param nullDefault the default value for a null predicate
     * @return a predicate that represents the logical NOT of the predicate
     * @since 3.18.0
     */
    public static <T> Predicate<T> not(final Predicate<T> predicate, final boolean nullDefault) {
        return input -> {
            final boolean result = predicate == null ? nullDefault : predicate.test(input);
            return !result;
        };
    }

    /**
     * Returns a predicate that performs a logical NOT operation on the provided predicate.
     * This is equivalent to calling {@code not(predicate, false)}, meaning a null
     * predicate is treated as always returning {@code false}.
     *
     * @param <T> the type of the input to the predicate
     * @param predicate the predicate to negate
     * @return a predicate that represents the logical NOT of the predicate
     * @since 3.18.0
     */
    public static <T> Predicate<T> not(final Predicate<T> predicate) {
        return not(predicate, false);
    }

    /**
     * Returns a predicate that performs a logical XOR (exclusive OR) operation on multiple predicates.
     * The returned predicate evaluates to {@code true} if an odd number of the provided 
     * predicates evaluate to {@code true}.
     * 
     * <p>Null predicates are handled based on the {@code nullDefault} parameter:
     * if {@code true}, null predicates are treated as always returning {@code true};
     * if {@code false}, null predicates are treated as always returning {@code false}.</p>
     *
     * @param <T> the type of the input to the predicates
     * @param nullDefault the default value for null predicates
     * @param predicates the predicates to XOR together
     * @return a predicate that represents the logical XOR of the predicates
     * @throws IllegalArgumentException if the predicates array is null or empty
     * @since 3.18.0
     */
    @SafeVarargs
    public static <T> Predicate<T> xor(final boolean nullDefault, final Predicate<T>... predicates) {
        Objects.requireNonNull(predicates, "Predicates array cannot be null");
        if (predicates.length == 0) {
            throw new IllegalArgumentException("At least one predicate must be provided");
        }
        
        return input -> {
            boolean result = false;
            for (final Predicate<T> predicate : predicates) {
                final boolean predicateResult = predicate == null ? nullDefault : predicate.test(input);
                result ^= predicateResult;
            }
            return result;
        };
    }

    /**
     * Returns a predicate that performs a logical XOR (exclusive OR) operation on multiple predicates.
     * This is equivalent to calling {@code xor(false, predicates)}, meaning null
     * predicates are treated as always returning {@code false}.
     *
     * @param <T> the type of the input to the predicates
     * @param predicates the predicates to XOR together
     * @return a predicate that represents the logical XOR of the predicates
     * @throws IllegalArgumentException if the predicates array is null or empty
     * @since 3.18.0
     */
    @SafeVarargs
    public static <T> Predicate<T> xor(final Predicate<T>... predicates) {
        return xor(false, predicates);
    }

    /**
     * Returns a predicate that performs a logical NOR operation on multiple predicates.
     * The returned predicate evaluates to {@code true} if all provided predicates 
     * evaluate to {@code false}. This is equivalent to {@code NOT(OR(...))}.
     * 
     * <p>Null predicates are handled based on the {@code nullDefault} parameter:
     * if {@code true}, null predicates are treated as always returning {@code true};
     * if {@code false}, null predicates are treated as always returning {@code false}.</p>
     *
     * @param <T> the type of the input to the predicates
     * @param nullDefault the default value for null predicates
     * @param predicates the predicates to NOR together
     * @return a predicate that represents the logical NOR of the predicates
     * @throws IllegalArgumentException if the predicates array is null or empty
     * @since 3.18.0
     */
    @SafeVarargs
    public static <T> Predicate<T> nor(final boolean nullDefault, final Predicate<T>... predicates) {
        Objects.requireNonNull(predicates, "Predicates array cannot be null");
        if (predicates.length == 0) {
            throw new IllegalArgumentException("At least one predicate must be provided");
        }
        
        return input -> {
            for (final Predicate<T> predicate : predicates) {
                final boolean result = predicate == null ? nullDefault : predicate.test(input);
                if (result) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Returns a predicate that performs a logical NOR operation on multiple predicates.
     * This is equivalent to calling {@code nor(false, predicates)}, meaning null
     * predicates are treated as always returning {@code false}.
     *
     * @param <T> the type of the input to the predicates
     * @param predicates the predicates to NOR together
     * @return a predicate that represents the logical NOR of the predicates
     * @throws IllegalArgumentException if the predicates array is null or empty
     * @since 3.18.0
     */
    @SafeVarargs
    public static <T> Predicate<T> nor(final Predicate<T>... predicates) {
        return nor(false, predicates);
    }

    /**
     * Returns a predicate that performs a logical NAND operation on multiple predicates.
     * The returned predicate evaluates to {@code true} if at least one provided predicate 
     * evaluates to {@code false}. This is equivalent to {@code NOT(AND(...))}.
     * 
     * <p>Null predicates are handled based on the {@code nullDefault} parameter:
     * if {@code true}, null predicates are treated as always returning {@code true};
     * if {@code false}, null predicates are treated as always returning {@code false}.</p>
     *
     * @param <T> the type of the input to the predicates
     * @param nullDefault the default value for null predicates
     * @param predicates the predicates to NAND together
     * @return a predicate that represents the logical NAND of the predicates
     * @throws IllegalArgumentException if the predicates array is null or empty
     * @since 3.18.0
     */
    @SafeVarargs
    public static <T> Predicate<T> nand(final boolean nullDefault, final Predicate<T>... predicates) {
        Objects.requireNonNull(predicates, "Predicates array cannot be null");
        if (predicates.length == 0) {
            throw new IllegalArgumentException("At least one predicate must be provided");
        }
        
        return input -> {
            for (final Predicate<T> predicate : predicates) {
                final boolean result = predicate == null ? nullDefault : predicate.test(input);
                if (!result) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Returns a predicate that performs a logical NAND operation on multiple predicates.
     * This is equivalent to calling {@code nand(false, predicates)}, meaning null
     * predicates are treated as always returning {@code false}.
     *
     * @param <T> the type of the input to the predicates
     * @param predicates the predicates to NAND together
     * @return a predicate that represents the logical NAND of the predicates
     * @throws IllegalArgumentException if the predicates array is null or empty
     * @since 3.18.0
     */
    @SafeVarargs
    public static <T> Predicate<T> nand(final Predicate<T>... predicates) {
        return nand(false, predicates);
    }

    /**
     * No instances needed.
     */
    private Predicates() {
        // empty
    }
}
