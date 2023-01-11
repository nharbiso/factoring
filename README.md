# Factoring Project

This project determines the nature of any given multivariable expression and factors into its component parts! I first completed a version of this implementation (`src/version1`) with my first introduction to computer science during my freshman year of high school. Upon coursework on data structures sophomore year, I then created a new documented version of the project (`src/version2`) utilizing my new knowledge.

To run the project, navigate to and run the `Factoring.java` file of either respective version. A display will pop up requesting an expression as input to be factored. Expressions must be entered as terms separated a plus or minus sign (without any space). Terms start with their numerical coefficient (if not 1) following by alphabetic characters with succeeding carats and numbers, representing variables to some power. Hence, an example valid input expression is '3x^2z^43-y^7+72'.

The currently supported factoring methods are:
- factoring multivariable binomials (e.g. 16x^8^y^8^-625z^4^ = (4x^4^y^4^+25z^2^)(2x^2^y^2^-5z)(2x^2^y^2^-5z))
- factoring multivariable expressions by grouping (e.g. 35x^2+^14xy-15xz-6yz = (5x+2y)(7x-3z))
- factoring single variable polynomials by the rational root theorem (e.g 35x^4^-163x^3^-89x^2^+139x+30 = (x-5)(x+1)(5x+1)(7x-6))
- factoring multivariable quadratics (e.g. 3x^2^+xy-14y^2^ = (x-2y)(3x+7y))
- factoring multivariable quartics into unfactorable quadratics (5x^4^+2x^3^+3x^2^-2x+1 = (5x^2^-3x+1)(x^2^+x+1))
