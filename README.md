# Factoring Project

This project determines the nature of any given multivariable expression and factors into its component parts! I first completed a version of this implementation (`src/version1`) with my first introduction to computer science during my freshman year of high school. Upon coursework on data structures sophomore year, I then created a new documented version of the project (`src/version2`) utilizing my new knowledge. I have recently reorganized the code in a new version (a continual work in progress!), with better documentation, organization, and readibility (`src/version3`).

To run the project, navigate to and run the `Factoring.java` file of any version. A display will pop up requesting an expression as input to be factored. Expressions must be entered as terms separated a plus or minus sign. Terms start with their numerical coefficient (if not 1) followed by alphabetic characters with succeeding carets and numbers, representing variables to some power. Hence, an example valid input expression is "3x^2z^43-y^7+72p-4", representing the expression 3x<sup>2</sup>z<sup>43</sup>-y<sup>7</sup>+72p-4.

The currently supported factoring methods are:
- factoring out a common term, such as 51x<sup>8</sup>y<sup>2</sup>-27x<sup>2</sup>y<sup>3</sup>z<sup>14</sup>+42x<sup>7</sup>y = 3x<sup>2</sup>y(17x<sup>6</sup>y-9y<sup>2</sup>z<sup>14</sup>+14x<sup>5</sup>)
- factoring multivariable binomials, such as 16x<sup>8</sup>y<sup>8</sup>-625z<sup>4</sup> = (4x<sup>4</sup>y<sup>4</sup>+25z<sup>2</sup>)(2x<sup>2</sup>y<sup>2</sup>+5z)(2x<sup>2</sup>y<sup>2</sup>-5z)
- factoring multivariable expressions by grouping, such as 35x<sup>2</sup>+14xy-15xz-6yz = (5x+2y)(7x-3z)
- factoring single variable polynomials by the rational root theorem, such as 35x<sup>4</sup>-163x<sup>3</sup>-89x<sup>2</sup>+139x+30 = (x-5)(x+1)(5x+1)(7x-6)
- factoring multivariable quadratics, such as 3x<sup>2</sup>+xy-14y<sup>2</sup> = (x-2y)(3x+7y)
- factoring multivariable quartics into unfactorable quadratics, such as 5x<sup>4</sup>+2x<sup>3</sup>+3x<sup>2</sup>-2x+1 = (5x<sup>2</sup>-3x+1)(x<sup>2</sup>+x+1)

Known bugs for v3: 
- freezes on x^3+x^2+1