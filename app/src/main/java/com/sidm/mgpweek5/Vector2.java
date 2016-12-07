package com.sidm.mgpweek5;

import static java.lang.Math.sqrt;

/**
 * Created by Foo on 3/12/2016.
 */

public class Vector2 {

    public float x, y;

    // Constructor
    public Vector2() {
        x = 0.f;
        y = 0.f;
    }

    public Vector2(Vector2 vector) {
        x = vector.x;
        y = vector.y;
    }

    public Vector2(float xVal, float yVal) {
        x = xVal;
        y = yVal;
    }

    // Set values
    public void Set(float xVal, float yVal) {
        x = xVal;
        y = yVal;
    }

    public void SetZero() {
        x = 0.f;
        y = 0.f;
    }

    public boolean IsZero() {
        if (this.x == 0.f && this.y == 0.f)
            return true;

        return false;
    }

    public float GetLength() {
        float lengthSquared = x * x + y * y;
        return (float)sqrt(lengthSquared);
    }

    public String ToString()
    {
        String str = new String();
        str += "[";
        str += String.valueOf(x);
        str += ",";
        str += String.valueOf(y);
        str += "]";
        return str;
    }

    public float GetLengthSquared() {
        return x * x + y * y;
    }

    // Math Operations
    public Vector2 Add(Vector2 other) {
        Vector2 result = new Vector2();
        result.x = this.x + other.x;
        result.y = this.y + other.y;

        return result;
    }

    public void AddToThis(Vector2 other) {
        this.x += other.x;
        this.y += other.y;
    }

    public Vector2 Subtract(Vector2 other) {
        Vector2 result = new Vector2();
        result.x = this.x - other.x;
        result.y = this.y - other.y;

        return result;
    }

    public void SubtractFromThis(Vector2 other) {
        this.x -= other.x;
        this.y -= other.y;
    }

    public Vector2 Multiply(float scalar) {
        Vector2 result = new Vector2();
        result.x = this.x * scalar;
        result.y = this.y * scalar;

        return result;
    }

    public void MultiplyWithThis(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    public Vector2 Divide(float scalar) {
        Vector2 result = new Vector2();
        result.x = this.x / scalar;
        result.y = this.y / scalar;

        return result;
    }

    public void DivideThis(float scalar) {
        this.x /= scalar;
        this.y /= scalar;
    }

    public float Dot(Vector2 other) {
        return (x * other.x) + (y * other.y);
    }

    // Normalize
    public void Normalize() {
        float length = GetLength();

        if (IsZero() || length == 0.f) {
            this.x = 0.f;
            this.y = 0.f;
            return;
        }

        this.x /= length;
        this.y /= length;
    }

    public Vector2 GetNormalized() {
        Vector2 result = this;
        result.Normalize();

        return result;
    }

}
