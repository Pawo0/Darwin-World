package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest {
    @Test
    void equalsTest() {
        Vector2d vector1 = new Vector2d(1, 2);
        Vector2d vector2 = new Vector2d(1, 2);
        Vector2d vector3 = new Vector2d(-4, 123);
        Vector2d vector4 = new Vector2d(-4, -123);
        Vector2d vectorNull = null;

        assertEquals(vector1, vector2);
        assertNotEquals(vector3, vector4);
        assertEquals(vector3, vector3);
        assertNotEquals(vector1, vectorNull);
        assertEquals(vectorNull, vectorNull);
    }
    @Test
    void toStringTest(){
        Vector2d vector1 = new Vector2d(-4,10);
        Vector2d vector2 = new Vector2d(0,0);

        assertEquals(vector1.toString(), "(-4,10)");
        assertEquals(vector2.toString(), "(0,0)");
    }
    @Test
    void precedesTest(){
        Vector2d vector1 = new Vector2d(10, 10);
        Vector2d vector2 = new Vector2d(-12,0);

        assertTrue(vector1.precedes(vector1));
        assertTrue(vector1.precedes(vector2));
        assertFalse(vector2.precedes(vector1));
    }
    @Test
    void followsTest(){
        Vector2d vector1 = new Vector2d(123, 0);
        Vector2d vector2 = new Vector2d(-3,0);

        assertTrue(vector1.follows(vector1));
        assertFalse(vector1.follows(vector2));
        assertTrue(vector2.follows(vector1));
    }
    @Test
    void upperRightTest(){
        Vector2d vector1 = new Vector2d(53, 4);
        Vector2d vector2 = new Vector2d(-3,7);
        Vector2d vector3 = new Vector2d(-12,-22);

        Vector2d upper_v1_v2 = new Vector2d(53,7);
        Vector2d upper_v1_v3 = new Vector2d(53,4);
        Vector2d upper_v2_v3 = new Vector2d(-3,7);


        assertEquals(vector1.upperRight(vector2), upper_v1_v2);
        assertEquals(vector1.upperRight(vector3), upper_v1_v3);
        assertEquals(vector2.upperRight(vector3), upper_v2_v3);
    }

    @Test
    void lowerLeftTest(){
        Vector2d vector1 = new Vector2d(53, 4);
        Vector2d vector2 = new Vector2d(-3,-9);
        Vector2d vector3 = new Vector2d(-12,-7);

        Vector2d lower_v1_v2 = new Vector2d(-3,-9);
        Vector2d lower_v1_v3 = new Vector2d(-12,-7);
        Vector2d lower_v2_v3 = new Vector2d(-12,-9);


        assertEquals(vector1.lowerLeft(vector2), lower_v1_v2);
        assertEquals(vector1.lowerLeft(vector3), lower_v1_v3);
        assertEquals(vector2.lowerLeft(vector3), lower_v2_v3);
    }

    @Test
    void addTest(){
        Vector2d vector1 = new Vector2d(4, 6);
        Vector2d vector2 = new Vector2d(-3,-9);
        Vector2d vector3 = new Vector2d(-11,3);

        Vector2d add_v1_v2 = new Vector2d(1,-3);
        Vector2d add_v1_v3 = new Vector2d(-7,9);
        Vector2d add_v2_v3 = new Vector2d(-14,-6);

        assertEquals(vector1.add(vector2), add_v1_v2);
        assertEquals(vector1.add(vector3), add_v1_v3);
        assertEquals(vector2.add(vector3), add_v2_v3);
    }
    @Test
    void subtractTest(){
        Vector2d vector1 = new Vector2d(4, 6);
        Vector2d vector2 = new Vector2d(-3,-9);
        Vector2d vector3 = new Vector2d(-11,3);

        Vector2d subtract_v1_v2 = new Vector2d(7,15);
        Vector2d subtract_v1_v3 = new Vector2d(15,3);
        Vector2d subtract_v2_v3 = new Vector2d(8,-12);

        assertEquals(vector1.subtract(vector2), subtract_v1_v2);
        assertEquals(vector1.subtract(vector3), subtract_v1_v3);
        assertEquals(vector2.subtract(vector3), subtract_v2_v3);
    }
    @Test
    void opositeTest(){
        Vector2d vector1 = new Vector2d(4, -6);
        Vector2d vector2 = new Vector2d(-3,0);

        Vector2d ops_v1 = new Vector2d(-4,6);
        Vector2d ops_v2 = new Vector2d(3,0);

        assertEquals(vector1.opposite(), ops_v1);
        assertEquals(vector2.opposite(), ops_v2);
    }

}
