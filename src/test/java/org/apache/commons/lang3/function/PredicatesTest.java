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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link Predicates}.
 */
class PredicatesTest {

    @Test
    void testFalsePredicate() {
        assertFalse(Predicates.falsePredicate().test(null));
        assertFalse(Predicates.falsePredicate().test(new Object()));
        final Predicate<String> stringPredicate = Predicates.falsePredicate();
        assertFalse(stringPredicate.test(null));
        assertFalse(stringPredicate.test(""));
    }

    @Test
    void testTruePredicate() {
        assertTrue(Predicates.truePredicate().test(null));
        assertTrue(Predicates.truePredicate().test(new Object()));
        final Predicate<String> stringPredicate = Predicates.truePredicate();
        assertTrue(stringPredicate.test(null));
        assertTrue(stringPredicate.test(""));
    }

    // Helper predicates for testing
    private static final Predicate<String> ALWAYS_TRUE = s -> true;
    private static final Predicate<String> ALWAYS_FALSE = s -> false;
    private static final Predicate<String> IS_NULL = s -> s == null;
    private static final Predicate<String> IS_NOT_NULL = s -> s != null;
    private static final Predicate<String> IS_EMPTY = s -> s != null && s.isEmpty();
    private static final Predicate<String> IS_NOT_EMPTY = s -> s != null && !s.isEmpty();

    @Test
    void testAndBasic() {
        // Test basic AND functionality
        final Predicate<String> andPredicate = Predicates.and(ALWAYS_TRUE, ALWAYS_TRUE);
        assertTrue(andPredicate.test("test"));
        
        final Predicate<String> andPredicateFalse = Predicates.and(ALWAYS_TRUE, ALWAYS_FALSE);
        assertFalse(andPredicateFalse.test("test"));
        
        final Predicate<String> andPredicateAllFalse = Predicates.and(ALWAYS_FALSE, ALWAYS_FALSE);
        assertFalse(andPredicateAllFalse.test("test"));
    }

    @Test
    void testAndShortCircuit() {
        // Test short-circuit evaluation - should stop at first false
        final Predicate<String> throwingPredicate = s -> {
            throw new RuntimeException("Should not be called due to short-circuit");
        };
        final Predicate<String> andPredicate = Predicates.and(ALWAYS_FALSE, throwingPredicate);
        assertFalse(andPredicate.test("test")); // Should not throw
    }

    @Test
    void testAndNullHandling() {
        // Test null handling with default false
        final Predicate<String> andWithNull = Predicates.and(ALWAYS_TRUE, null);
        assertFalse(andWithNull.test("test"));
        
        // Test null handling with default true
        final Predicate<String> andWithNullTrue = Predicates.and(true, ALWAYS_TRUE, null);
        assertTrue(andWithNullTrue.test("test"));
        
        final Predicate<String> andWithNullFalse = Predicates.and(false, ALWAYS_TRUE, null);
        assertFalse(andWithNullFalse.test("test"));
    }

    @Test
    void testAndMultiplePredicates() {
        final Predicate<String> andMultiple = Predicates.and(IS_NOT_NULL, IS_NOT_EMPTY, s -> s.length() > 2);
        assertTrue(andMultiple.test("test"));
        assertFalse(andMultiple.test(""));
        assertFalse(andMultiple.test(null));
        assertFalse(andMultiple.test("ab"));
    }

    @Test
    void testAndValidation() {
        assertThrows(NullPointerException.class, () -> Predicates.and((Predicate<String>[]) null));
        assertThrows(IllegalArgumentException.class, () -> Predicates.and());
    }

    @Test
    void testOrBasic() {
        // Test basic OR functionality
        final Predicate<String> orPredicate = Predicates.or(ALWAYS_FALSE, ALWAYS_TRUE);
        assertTrue(orPredicate.test("test"));
        
        final Predicate<String> orPredicateAllFalse = Predicates.or(ALWAYS_FALSE, ALWAYS_FALSE);
        assertFalse(orPredicateAllFalse.test("test"));
        
        final Predicate<String> orPredicateAllTrue = Predicates.or(ALWAYS_TRUE, ALWAYS_TRUE);
        assertTrue(orPredicateAllTrue.test("test"));
    }

    @Test
    void testOrShortCircuit() {
        // Test short-circuit evaluation - should stop at first true
        final Predicate<String> throwingPredicate = s -> {
            throw new RuntimeException("Should not be called due to short-circuit");
        };
        final Predicate<String> orPredicate = Predicates.or(ALWAYS_TRUE, throwingPredicate);
        assertTrue(orPredicate.test("test")); // Should not throw
    }

    @Test
    void testOrNullHandling() {
        // Test null handling with default false
        final Predicate<String> orWithNull = Predicates.or(ALWAYS_FALSE, null);
        assertFalse(orWithNull.test("test"));
        
        // Test null handling with default true
        final Predicate<String> orWithNullTrue = Predicates.or(true, ALWAYS_FALSE, null);
        assertTrue(orWithNullTrue.test("test"));
        
        final Predicate<String> orWithNullFalse = Predicates.or(false, ALWAYS_FALSE, null);
        assertFalse(orWithNullFalse.test("test"));
    }

    @Test
    void testOrMultiplePredicates() {
        final Predicate<String> orMultiple = Predicates.or(IS_NULL, IS_EMPTY, s -> s.equals("special"));
        assertTrue(orMultiple.test(null));
        assertTrue(orMultiple.test(""));
        assertTrue(orMultiple.test("special"));
        assertFalse(orMultiple.test("test"));
    }

    @Test
    void testOrValidation() {
        assertThrows(NullPointerException.class, () -> Predicates.or((Predicate<String>[]) null));
        assertThrows(IllegalArgumentException.class, () -> Predicates.or());
    }

    @Test
    void testNotBasic() {
        final Predicate<String> notTrue = Predicates.not(ALWAYS_TRUE);
        assertFalse(notTrue.test("test"));
        
        final Predicate<String> notFalse = Predicates.not(ALWAYS_FALSE);
        assertTrue(notFalse.test("test"));
    }

    @Test
    void testNotNullHandling() {
        // Test null handling with default false
        final Predicate<String> notNull = Predicates.not(null);
        assertTrue(notNull.test("test")); // NOT(false) = true
        
        // Test null handling with default true
        final Predicate<String> notNullTrue = Predicates.not(null, true);
        assertFalse(notNullTrue.test("test")); // NOT(true) = false
        
        final Predicate<String> notNullFalse = Predicates.not(null, false);
        assertTrue(notNullFalse.test("test")); // NOT(false) = true
    }

    @Test
    void testXorBasic() {
        // Test basic XOR functionality
        final Predicate<String> xorTrueFalse = Predicates.xor(ALWAYS_TRUE, ALWAYS_FALSE);
        assertTrue(xorTrueFalse.test("test"));
        
        final Predicate<String> xorFalseTrue = Predicates.xor(ALWAYS_FALSE, ALWAYS_TRUE);
        assertTrue(xorFalseTrue.test("test"));
        
        final Predicate<String> xorTrueTrue = Predicates.xor(ALWAYS_TRUE, ALWAYS_TRUE);
        assertFalse(xorTrueTrue.test("test"));
        
        final Predicate<String> xorFalseFalse = Predicates.xor(ALWAYS_FALSE, ALWAYS_FALSE);
        assertFalse(xorFalseFalse.test("test"));
    }

    @Test
    void testXorMultiplePredicates() {
        // XOR with odd number of trues should be true
        final Predicate<String> xorThreeTrue = Predicates.xor(ALWAYS_TRUE, ALWAYS_TRUE, ALWAYS_TRUE);
        assertTrue(xorThreeTrue.test("test"));
        
        // XOR with even number of trues should be false
        final Predicate<String> xorTwoTrue = Predicates.xor(ALWAYS_TRUE, ALWAYS_TRUE, ALWAYS_FALSE, ALWAYS_FALSE);
        assertFalse(xorTwoTrue.test("test"));
        
        // XOR with one true should be true
        final Predicate<String> xorOneTrue = Predicates.xor(ALWAYS_FALSE, ALWAYS_TRUE, ALWAYS_FALSE);
        assertTrue(xorOneTrue.test("test"));
    }

    @Test
    void testXorNullHandling() {
        // Test null handling with default false
        final Predicate<String> xorWithNull = Predicates.xor(ALWAYS_TRUE, null);
        assertTrue(xorWithNull.test("test")); // true XOR false = true
        
        // Test null handling with default true
        final Predicate<String> xorWithNullTrue = Predicates.xor(true, ALWAYS_TRUE, null);
        assertFalse(xorWithNullTrue.test("test")); // true XOR true = false
    }

    @Test
    void testXorValidation() {
        assertThrows(NullPointerException.class, () -> Predicates.xor((Predicate<String>[]) null));
        assertThrows(IllegalArgumentException.class, () -> Predicates.xor());
    }

    @Test
    void testNorBasic() {
        // Test basic NOR functionality
        final Predicate<String> norFalseFalse = Predicates.nor(ALWAYS_FALSE, ALWAYS_FALSE);
        assertTrue(norFalseFalse.test("test")); // NOR(false, false) = true
        
        final Predicate<String> norTrueFalse = Predicates.nor(ALWAYS_TRUE, ALWAYS_FALSE);
        assertFalse(norTrueFalse.test("test")); // NOR(true, false) = false
        
        final Predicate<String> norTrueTrue = Predicates.nor(ALWAYS_TRUE, ALWAYS_TRUE);
        assertFalse(norTrueTrue.test("test")); // NOR(true, true) = false
    }

    @Test
    void testNorNullHandling() {
        // Test null handling with default false
        final Predicate<String> norWithNull = Predicates.nor(ALWAYS_FALSE, null);
        assertTrue(norWithNull.test("test")); // NOR(false, false) = true
        
        // Test null handling with default true
        final Predicate<String> norWithNullTrue = Predicates.nor(true, ALWAYS_FALSE, null);
        assertFalse(norWithNullTrue.test("test")); // NOR(false, true) = false
    }

    @Test
    void testNorValidation() {
        assertThrows(NullPointerException.class, () -> Predicates.nor((Predicate<String>[]) null));
        assertThrows(IllegalArgumentException.class, () -> Predicates.nor());
    }

    @Test
    void testNandBasic() {
        // Test basic NAND functionality
        final Predicate<String> nandTrueTrue = Predicates.nand(ALWAYS_TRUE, ALWAYS_TRUE);
        assertFalse(nandTrueTrue.test("test")); // NAND(true, true) = false
        
        final Predicate<String> nandTrueFalse = Predicates.nand(ALWAYS_TRUE, ALWAYS_FALSE);
        assertTrue(nandTrueFalse.test("test")); // NAND(true, false) = true
        
        final Predicate<String> nandFalseFalse = Predicates.nand(ALWAYS_FALSE, ALWAYS_FALSE);
        assertTrue(nandFalseFalse.test("test")); // NAND(false, false) = true
    }

    @Test
    void testNandNullHandling() {
        // Test null handling with default false
        final Predicate<String> nandWithNull = Predicates.nand(ALWAYS_TRUE, null);
        assertTrue(nandWithNull.test("test")); // NAND(true, false) = true
        
        // Test null handling with default true
        final Predicate<String> nandWithNullTrue = Predicates.nand(true, ALWAYS_TRUE, null);
        assertFalse(nandWithNullTrue.test("test")); // NAND(true, true) = false
    }

    @Test
    void testNandValidation() {
        assertThrows(NullPointerException.class, () -> Predicates.nand((Predicate<String>[]) null));
        assertThrows(IllegalArgumentException.class, () -> Predicates.nand());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "test", "null"})
    void testLogicalOperationsConsistency(final String input) {
        final String testInput = "null".equals(input) ? null : input;
        
        // Test De Morgan's laws
        final Predicate<String> p1 = IS_NULL;
        final Predicate<String> p2 = IS_EMPTY;
        
        // NOT(A AND B) = (NOT A) OR (NOT B)
        final Predicate<String> notAndPredicate = Predicates.not(Predicates.and(p1, p2));
        final Predicate<String> notOrNotPredicate = Predicates.or(Predicates.not(p1), Predicates.not(p2));
        
        final boolean notAndResult = notAndPredicate.test(testInput);
        final boolean notOrNotResult = notOrNotPredicate.test(testInput);
        assertTrue(notAndResult == notOrNotResult, 
            "De Morgan's law NOT(A AND B) = (NOT A) OR (NOT B) violated for input: " + input);
        
        // NOT(A OR B) = (NOT A) AND (NOT B)
        final Predicate<String> notOrPredicate = Predicates.not(Predicates.or(p1, p2));
        final Predicate<String> notAndNotPredicate = Predicates.and(Predicates.not(p1), Predicates.not(p2));
        
        final boolean notOrResult = notOrPredicate.test(testInput);
        final boolean notAndNotResult = notAndNotPredicate.test(testInput);
        assertTrue(notOrResult == notAndNotResult, 
            "De Morgan's law NOT(A OR B) = (NOT A) AND (NOT B) violated for input: " + input);
    }

    @Test
    void testComplexLogicalExpressions() {
        // Test complex expression: (IS_NULL OR IS_EMPTY) AND IS_NOT_NULL
        final Predicate<String> complex1 = Predicates.and(
            Predicates.or(IS_NULL, IS_EMPTY),
            IS_NOT_NULL
        );
        
        assertTrue(complex1.test("")); // empty string
        assertFalse(complex1.test(null)); // null
        assertFalse(complex1.test("test")); // non-empty string
        
        // Test complex expression: NOT(IS_NULL XOR IS_EMPTY)
        final Predicate<String> complex2 = Predicates.not(
            Predicates.xor(IS_NULL, IS_EMPTY)
        );
        
        assertFalse(complex2.test(null)); // IS_NULL=true, IS_EMPTY=false (XOR=true, NOT=false)
        assertFalse(complex2.test("")); // IS_NULL=false, IS_EMPTY=true (XOR=true, NOT=false)
        assertTrue(complex2.test("test")); // IS_NULL=false, IS_EMPTY=false (XOR=false, NOT=true)
    }

    @Test
    void testEdgeCases() {
        // Test with single predicate
        final Predicate<String> singleAnd = Predicates.and(ALWAYS_TRUE);
        assertTrue(singleAnd.test("test"));
        
        final Predicate<String> singleOr = Predicates.or(ALWAYS_FALSE);
        assertFalse(singleOr.test("test"));
        
        final Predicate<String> singleXor = Predicates.xor(ALWAYS_TRUE);
        assertTrue(singleXor.test("test"));
        
        final Predicate<String> singleNor = Predicates.nor(ALWAYS_FALSE);
        assertTrue(singleNor.test("test"));
        
        final Predicate<String> singleNand = Predicates.nand(ALWAYS_TRUE);
        assertFalse(singleNand.test("test"));
    }

    @Test
    void testPerformanceConsistency() {
        // Create predicates that should have same behavior as manual implementation
        final Predicate<String> manualAnd = s -> ALWAYS_TRUE.test(s) && ALWAYS_FALSE.test(s);
        final Predicate<String> utilityAnd = Predicates.and(ALWAYS_TRUE, ALWAYS_FALSE);
        
        final String testValue = "performance test";
        
        // Test that both produce same result
        final boolean manualResult = manualAnd.test(testValue);
        final boolean utilityResult = utilityAnd.test(testValue);
        
        assertTrue(manualResult == utilityResult, "Manual and utility implementations should produce same result");
        assertFalse(utilityResult); // Both should be false
    }
}
