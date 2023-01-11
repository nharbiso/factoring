# Factoring Project

This project determines the nature of any given multivariable expression and factors into its component parts! I first completed a version of this implementation (`src/version1`) with my first introduction to computer science during my freshman year of high school. Upon coursework on data structures sophomore year, I then created a new documented version of the project (`src/version2`) utilizing my new knowledge.

To run the project, navigate to and run the `Factoring.java` file of either respective version. A display will pop up requesting an expression as input to be factored. Expressions must be entered as terms separated a plus or minus sign (without any space). Terms start with their numerical coefficient (if not 1) following by alphabetic characters with succeeding carats and numbers, representing variables to some power. Hence, an example valid input expression is '3x^2z^43-y^7+72'.

The currently supported factoring methods are:
- factoring multivariable binomials (e.g. 16x<sup>8</sup>y<sup>8</sup>-625z<sup>4</sup> = (4x<sup>4</sup>y<sup>4</sup>+25z<sup>2</sup>)(2x<sup>2</sup>y<sup>2</sup>-5z)(2x<sup>2</sup>y<sup>2</sup>-5z))
- factoring multivariable expressions by grouping (e.g. 35x<sup>2</sup>+14xy-15xz-6yz = (5x+2y)(7x-3z))
- factoring single variable polynomials by the rational root theorem (e.g 35x<sup>4</sup>-163x<sup>3</sup>-89x<sup>2</sup>+139x+30 = (x-5)(x+1)(5x+1)(7x-6))
- factoring multivariable quadratics (e.g. 3x<sup>2</sup>+xy-14y<sup>2</sup> = (x-2y)(3x+7y))
- factoring multivariable quartics into unfactorable quadratics (5x<sup>4</sup>+2x<sup>3</sup>+3x<sup>2</sup>-2x+1 = (5x<sup>2</sup>-3x+1)(x<sup>2</sup>+x+1))
