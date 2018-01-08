Name: Viet Duong
CSC 240 Project 1

In order to run the code in Windows Cmd or MacOS Terminal, first compile all the the .java source codes:
	javac Apriori.java
	javac FPTreeNode.java
	javac Header.java
	javac HeaderComparator.java
	javac FPGrowth.java
	javac ImprovedApriori.java

Then make sure the data file "adult.data.txt" is in the same directory as the java source codes, and type:
	java Apriori adult.data.txt 0.6
	java FPGrowth adult.data.txt 0.6
	java ImprovedApriori adult.data.txt 0.6

Also, to test the algorithms with the dataset and min_sup of your choice, after compiling the code, use the command with the following format:
	java <Algorithm> <data_file_name> <min_sup>

*******************
Sample output:

Apriori.java, with min_sup = 0.6
{0}
{<=50K}
{White}
{Private}
{United-States}
{Male}
{0,<=50K}
{0,White}
{0,Private}
{0,United-States}
{0,Male}
{<=50K,White}
{<=50K,United-States}
{White,United-States}
{Private,United-States}
{0,<=50K,White}
{0,<=50K,United-States}
{0,White,United-States}
{0,Private,United-States}
Runtime: 116.116sec
*******************

FPGrowth.java, with min_sup = 0.6:
{0,White}
{United-States}
{United-States,0,Private}
{<=50K}
{Male}
{<=50K,White}
{United-States,White}
{0,Private}
{United-States,Private}
{0,<=50K,United-States}
{0}
{White}
{<=50K,0}
{0,United-States}
{United-States,0,White}
{0,Male}
{0,<=50K,White}
{Private}
{<=50K,United-States}
Runtime: 0.592sec
*******************

Apriori.java, with min_sup = 0.6
{White}
{Male}
{Private}
{<=50K}
{United-States}
{0}
{White,<=50K}
{White,United-States}
{White,0}
{Male,0}
{Private,United-States}
{Private,0}
{<=50K,United-States}
{<=50K,0}
{United-States,0}
{White,<=50K,0}
{White,United-States,0}
{Private,United-States,0}
{<=50K,United-States,0}
Runtime: 0.67sec