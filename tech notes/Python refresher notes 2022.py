#####Python
Python Basics
Python Lists
	- Subsetting Lists
	- Slicing and Dicing
	- Manipulating Lists
		- Explicit and reference-based list copies
	- Functions , a piece of reusable code, aim at solving a particular task
	- Methods - functions that belong to objects
	- In Python everything is an object , and depending on the type, every object has associated methods
	- Packages - A directory of Python scripts (the scripts/modules specify functions, methods and types)
		- e.g. Numpy, Matplotlib, scikit-learn
		from scipy.linalg import inv as my_inv --- import the function inv(), which is in the linalg subpackage of the scipy package.
    - NumPy - Numeric Python (calculations over entire arrays)
    - import numpy as np
	
	Data Visualizations
	- import matplotlib.pyplot as plt
	Numpy Boolean Operators
	- bmi[np.logical_and(bmi > 21 , bmi < 22)]
	
	Accessing data in dataframes
	cars.loc['IN', 'cars_per_cap']
    cars.iloc[3, 0]

    cars.loc[['IN', 'RU'], 'cars_per_cap']
    cars.iloc[[3, 4], 0]

    cars.loc[['IN', 'RU'], ['cars_per_cap', 'country']]
    cars.iloc[[3, 4], [0, 1]]
	
	
	Loops:
	- for var in seq:
	     expression
	- Example
		areas = [11.25, 18.0, 20.0, 10.75, 9.50]
		for index,y in enumerate(areas) :
			print("room" + str(index) + ":" + str(y))
			
    Iterate over a dictionary
	 - for key, val in my_dict.items():   -- dictionaries use a method to iterate
	Iterate over a Numpy arrays
	 -  for val in np.nditer(my_array):   -- numpy arrays use a function to iterate
	Iterate over a Pandas Dataframe
	 - for val, row in data.iterrows():
	 - Example
		for lab, row in cars.iterrows() :
			print(lab +": "+ str(row['cars_per_cap']))
	    
		for i, row in cars.iterrows() :
		    cars.loc[i,"COUNTRY"] = row["country"].upper()
	 
      # Use .apply(str.upper)
            cars["COUNTRY"] = cars["country"].apply(str.upper)
	 
	 
	 
	 
	 