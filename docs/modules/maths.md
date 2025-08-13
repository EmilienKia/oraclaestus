Maths module
================

This maths module provides basic mathematical functions to entities. It basically wraps the Java `Math` class features.

In a general way, when functions are defined for a specific number type, this type is expected as parameters and will be returned.
For example, functions 'pow' or 'log' will expect a `float` as parameter and will return a `float'.

Functions that expects a 'number' can accept any numeric type (`int' or 'float'). Except specifically specified, 
the type of the first parameter will be used as reference type. Then all the parameters will be cast to this type and
this type will be returned.

### Available functions

- `abs(value: number) : number`: Returns the absolute value of a number.

- `rand(min: number, max: number) : number`: Returns a random number between `min` and `max` (inclusive).'
- `randGaussian(mean: float, stdev: float) : float`: Returns a random number following a Gaussian distribution with the specified mean and standard deviation.

- `cos(value: float) : float`: Returns the cosine of a value in radians.
- `cosh(value: float) : float`: Returns the hyperbolic cosine of a value in radians.
- `sin(value: float) : float`: Returns the sine of a value in radians.
- `sinh(value: float) : float`: Returns the hyperbolic sine of a value in radians.
- `tan(value: float) : float`: Returns the tangent of a value in radians.
- `tanh(value: float) : float`: Returns the hyperbolic tangent of a value in radians.
- `acos(value: float) : float`: Returns the arc cosine of a value in radians.
- `asin(value: float) : float`: Returns the arc sine of a value in radians.
- `atan(value: float) : float`: Returns the arc tangent of a value in radians.
- `atan2(y: float, x: float) : float`: Returns the arc tangent of the quotient of its arguments, in radians.
- `hypot(x: float, y: float) : float`: Returns the length of the hypotenuse of a right triangle with legs of lengths `x` and `y`.
- `toDegrees(value: float) : float`: Converts an angle measured in radians to an approximately equivalent angle measured in degrees.
- `toRadians(value: float) : float`: Converts an angle measured in degrees to an approximately equivalent angle measured in radians.

- `ceil(value: float) : float`: Returns the smallest integer that is greater than or equal to the argument and is equal to a mathematical integer.
- `ceilDiv(dividend: int, divisor: int) : int`: Returns the smallest integer that is greater than or equal to the result of dividing `dividend` by `divisor`.
- `ceilMod(dividend: int, divisor: int) : int`: Returns the smallest integer that is greater than or equal to the result of dividing `dividend` by `divisor`, and then multiplying the result by `divisor`.
- `floor(value: float) : float`: Returns the largest integer that is less than or equal to the argument and is equal to a mathematical integer.
- `floorDiv(dividend: int, divisor: int) : int`: Returns the largest integer that is less than or equal to the result of dividing `dividend` by `divisor`.
- `floorMod(dividend: int, divisor: int) : int`: Returns the largest integer that is less than or equal to the result of dividing `dividend` by `divisor`, and then multiplying the result by `divisor`.
- `clamp(value: number, min: number, max: number) : number`: Returns the value clamped to the range [min, max]. If value is less than min, returns min. If value is greater than max, returns max. Otherwise, returns value.
- `fma(a: float, b: float, c: float) : float`: Returns the fused multiply-add of `a`, `b`, and `c`, which is equivalent to `a * b + c` but computed in a single step to reduce rounding errors.
- `getExponent(value: float) : int`: Returns the unbiased exponent of the floating-point value, which is the exponent of the value in its normalized form.
- `rint(value: float) : float`: Returns the floating-point value that is closest in value to the argument and is equal to a mathematical integer.
- `round(value: float) : int`: Returns the closest integer to the argument, with ties rounding to the nearest even integer.

- `exp(value: float) : float`: Returns Euler's number raised to the power of the argument.
- `expm1(value: float) : float`: Returns `e^value - 1`, which is more accurate than `exp(value) - 1` for small values of `value`.
- `log(value: float) : float`: Returns the natural logarithm (base e)
- `log10(value: float) : float`: Returns the base 10 logarithm of the argument.
- `log1p(value: float) : float`: Returns the natural logarithm of `1 + value`, which is more accurate than `log(1 + value)` for small values of `value`.
- `pow(value: float, exponent: float) : float`: Returns the value raised to the power of the exponent.
- `cbrt(value: float) : float`: Returns the cube root of the argument.
- `sqrt(value: float) : float`: Returns the square root of the argument.

- `scalb(value: float, scaleFactor: int) : float`: Returns the value multiplied by 2 raised to the power of `scaleFactor`, which is equivalent to multiplying the value by `pow(2, scaleFactor)`.

- `max(a: number, b: number) : number`: Returns the greater of two values.
- `min(a: number, b: number) : number`: Returns the smaller of two values.

