import java.util.*;
import java.io.*;

// Main class
public class HangarManagement{
	// Generates one of three possible funds amounts
	static double FundingGenerator() {
		double funds = Math.random();
		
		if (funds <= 0.33)
			return 14000.0;
		else if (funds > 0.33 && funds <= 0.66)
			return 11000.0;
		else
			return 13000.0;
	}
	// Generates one of three possible personnel numbers
	static int EngineerGenerator() {
		double engineers = Math.random();
		
		if (engineers <= 0.33)
			return 3500;
		else if (engineers > 0.33 && engineers <= 0.66)
			return 4500;
		else
			return 2500;
	}
	
	public static void main (String[] args) {
		// Initialize the two objects from the other two classes, for the Resources one, the random engineer,
		// as well as funds numbers, and time (in months) will be passed to the constructor.
		Resources myObj = new Resources(FundingGenerator(), EngineerGenerator(), 12);
		Dialogue dial = new Dialogue();
		
		dial.Intro();
		
		for (int i = 0; i < 12; i++) {
			myObj.managementMenu();
		}
		myObj.printReport();
	}
}



// Class dealing with the operations on the resources of the hangar, as well as the actions
class Resources{
	double money;
	int engineers, engineersTaken = 0, temporalWorkers = 0;
	int time;
	boolean f22R = false, f35AL = false, f15EX = false, chengdu = false;
	boolean working = false, hiringEngineers = false;
	String researchedPlane = "";
	int[] aircraft = {0, 0, 0, 0, 0, 0, 0, 0};
	boolean[] researchingPlane = {false, false, false, false};

	public Resources(double money, int engineers, int time) {
		this.money = money;
		this.engineers = engineers;
		this.time = time;
	}

	// Most essential part of the program where many methods dealing with the hangar actions are called
	public void managementMenu() {
		Scanner in = new Scanner(System.in);
		int mngChoice;
		Dialogue dial = new Dialogue();

		do {
			dial.resourceStatus(money, engineers, time);
			
			System.out.println("What do you wish to do today?");
			System.out.println("Please pick one of the following:");
			System.out.printf("%-20s", "1. Produce Planes");
			System.out.printf("%-20s %n", "2. Research");
			System.out.printf("%-20s", "3. Trade");
			System.out.printf("%-20s %n", "4. Instructions");
			System.out.printf("%-20s", "5. Hangar Status");
			System.out.printf("%-20s %n", "6. Complete day (will spend 1 month)");
			
			mngChoice = in.nextInt();
			in.nextLine();
			
			switch (mngChoice) {
			case 1:
				producePlanes(f22R, f35AL, f15EX, chengdu);
				break;
			case 2:
				researchMenu();
				break;
			case 3:
				tradeMenu();
				break;
			case 4:
				dial.Instructions();
				break;
			case 5:
				dial.showHangar(aircraft);
				break;
			}
		}while(mngChoice != 6);
		
		engineerWorking();
		dayComplete();
		accidentChance();
	}

	// Method that tracks the time that passes as the user progresses on the hangar production, every day completed
	// spends a month, if no more months are left to work, a congratulation message is displayed.
	public void dayComplete() {
		time -= 1;
		
		System.out.println("All monthly activity has been decided for the day.");
		
		if (time >= 1) {
			System.out.println("");
			System.out.println("One month has passed...");
		}
		else {
			System.out.println("\nCongratulations Manager! You have finally fulfilled your service in the hangar");
			System.out.println("Your service report has been recorded and can be found on a separate file.");
		}
	}

	// Method that deals with plane production, as new planes are discovered, they become available in the menu
	public void producePlanes(boolean f22, boolean f35, boolean f15, boolean cheng) {
		Scanner pro = new Scanner(System.in);
		
		int planeChoice;
		
		System.out.println("<< Plane Production >>");
		System.out.println("Please pick which planes you want to send for manufacturing:");
		System.out.printf("%-30s", "1. F-15E Strike Eagle");
		System.out.printf("%-30s %n", "2. AC-130W Stinger II");
		System.out.printf("%-30s", "3. F-16 Fighting Falcon");
		System.out.printf("%-30s %n", "4. MQ-1B Predator");
		
		if (f22)
			System.out.printf("%-30s", "5. F-22 Raptor");
		
		if (f35)
			System.out.printf("%-30s %n", "6. F-35A Lightning II");
		
		if (f15)
			System.out.printf("%-30s", "7. F-15EX");
			
		if (cheng)
			System.out.printf("%-30s", "8. Chengdu J-20");
		
		System.out.println("");
		
		planeChoice = pro.nextInt();
		pro.nextLine();

		// Switch statement that calls the "planebuy" method depending on user input, and depending if the
		// plane is available
		switch (planeChoice) {
		case 1:
			planeBuy("F-15E Strike Eagle", 300, 400);
			break;
		case 2:
			planeBuy("AC-130W Stinger II", 350, 450);
			break;
		case 3:
			planeBuy("F-16 Fighting Falcon", 400, 500);
			break;
		case 4:
			planeBuy("MQ-1B Predator", 450, 600);
			break;
		case 5:
			if (f22R)
				planeBuy("F-22 Raptor", 500, 650);
			break;
		case 6:
			if (f35AL)
				planeBuy("F-35A Lightning II", 550, 700);
			break;
		case 7:
			if (f15EX)
				planeBuy("F-15EX", 600, 800);
			break;
		case 8:
			if (chengdu)
				planeBuy("Chengdu J-20", 650, 1000);
			break;
		}
	}

	// Method that actually performs the buying action according to the arguments given by the previous caller method
	public void planeBuy(String plane, int planeCost, int engies) {
		Scanner in = new Scanner(System.in);
		Dialogue dial = new Dialogue();

		// To check whether the action is possible with the resources available, "pseudo" variables are declared to
		// perform a test of the desired purchase before making the actual transaction
		int pseudoEngineers = engineers;
		double pseudoBalance = money;
		char userConfirmation, amountConfirmation;
		int planesAmount;
		
		System.out.println("<< " + plane + " >>");
		System.out.print("You have selected " + plane + " which will cost you $" + planeCost + " (M) and send " + engies + " ");
		System.out.println("engineers to complete the action.");
		System.out.println("Would you like to manufacture this airplane? Please enter (Y/N):");
		
		userConfirmation = in.next().charAt(0);
		in.nextLine();

		// Check user input and make calculations to see if purchase is doable
		if (userConfirmation == 'y' || userConfirmation == 'Y') {
			pseudoBalance -= planeCost;
			pseudoEngineers -= engies;

			// If "pseudoBalance" is negative, an insufficient amount message is displayed to the user
			if (pseudoBalance < 0)
				dial.insufficientFunds();
			else if (pseudoEngineers < 0)
				dial.insufficientEngineers();
			else {
				System.out.print("Do you wish to buy just (1) unit? (Y/N) (If you press 'N' you will receive a prompt that will ");
				System.out.println("enable you to get more units.)");
				
				amountConfirmation = in.next().charAt(0);
				in.nextLine();
				
				if (amountConfirmation == 'n' || amountConfirmation == 'N') {
					System.out.println("Please enter the amount of planes that you would like to buy:");
					
					planesAmount = in.nextInt();
					in.nextLine();
					
					pseudoBalance = money;
					pseudoBalance -= (planeCost * planesAmount);
					pseudoEngineers = engineers;
					pseudoEngineers -= (engies * planesAmount);
				
					if (pseudoBalance < 0)
						dial.insufficientFunds();
					else if (pseudoEngineers < 0)
						dial.insufficientEngineers();
					else {
						money -= money - pseudoBalance;
						engineers -= engineers - pseudoEngineers;
						engineersTaken += (engies * planesAmount);
						
						for (int i = 0; i < planesAmount; i++)
							planeAddition(plane);
						
						System.out.println("Your order of " + planesAmount + " (" + plane + ") units is complete.");
					}
				
				}
				else {
				money -= planeCost;
				engineers -= engies;
				engineersTaken += engies;
				
				planeAddition(plane);
				
				System.out.println("Your order of a " + plane + " unit has been completed");
				}
			}
		}
	}

	// Method that deals with the plane research aspect of the program
	public void researchMenu() {
		Scanner in = new Scanner(System.in);
		int researchChoice;
		
		System.out.println("<< Hangar Research >>");
		System.out.println("Please choose the project you wish to research.");
		System.out.printf("%-30s", "1. F-22 Raptor");

		// If statements that display available planes for research if the previous model was researched
		if (f22R)
			System.out.printf("%-30s %n", "2. F-35A Lightning II");
		
		if (f35AL)
			System.out.printf("%-30s", "3. F-15EX");
		
		if (f15EX)
			System.out.printf("%-30s", "4. Chengdu J-20");
		
		System.out.println("");
		
		researchChoice = in.nextInt();
		in.nextLine();

		// Switch statement that calls the "researchAction" method with specific plane arguments depending on the userInput
		switch (researchChoice) {
		case 1:
			if(researchingPlane[0])
				System.out.println("Error: This plane is on or has already been researched");
			else
				researchAction("F-22 Raptor", 850, 800);
			break;
		case 2:
			if(researchingPlane[1])
				System.out.println("Error: This plane is on or has already been researched");
			else if (f22R)
				researchAction("F-35A Lightning II", 900, 950);
			break;
		case 3:
			if(researchingPlane[2])
				System.out.println("Error: This plane is on or has already been researched");
			else if (f35AL)
				researchAction("F-15EX", 1050, 1100);
			break;
		case 4:
			if (researchingPlane[3])
				System.out.println("Error: This plane is on or has already been researched");
			else if (f15EX)
				researchAction("Chengdu J-20", 1200, 1250);
			break;
		}
	}

	// Method that displays a confirmation message and calls the "buyAction" method
	public void researchAction(String plane, int researchCost, int engiesC) {
		Dialogue dial = new Dialogue();
		
		System.out.println("<< " + plane + " >>");
		System.out.print("You have selected to research " + plane + "which will cost you $" + researchCost + "(M), send ");
		System.out.println(engiesC + " engineers to work on the project, and take 1 month to complete.");
		System.out.println("Would you like to fund the project? Please enter (Y/N)");
		
		buyAction(researchCost, engiesC, plane, 0);
	}

	// This method handles the amount of engineers that were set out to work, as well as making sure that that same
	// amount comes back to availability once 1 month passes (unless an event disrupts this process)
	public void engineerWorking() {
		// these operations return the working engineers back to availability and set "engineersTaken" back to zero.
		engineers += engineersTaken;
		engineersTaken = 0;

		// if statements that handles hired workers, if they were working, they get removed from the overall number
		// of engineers and the working variable is set back to false
		if (working) {
			engineers -= temporalWorkers;
			working = false;
		}
		
		if(researchedPlane.equals("F-22 Raptor"))
			f22R = true;
		else if(researchedPlane.equals("F-35A Lightning II"))
			f35AL = true;
		else if(researchedPlane.equals("F-15EX"))
			f15EX = true;
		else if(researchedPlane.equals("Chengdu J-20"))
			chengdu = true;
	}

	// Method that performs buying operations for both engineer hiring and research
	public void buyAction(int myCost, int myEngieCost, String myPlane, int engieAdd) {
		Scanner in = new Scanner(System.in);
		Dialogue dial = new Dialogue();
		
		char userChoice;
		double thePseudoMoney = money;
		int thePseudoEngineers = engineers;
		
		userChoice = in.next().charAt(0);
		
		if (userChoice == 'y' || userChoice == 'Y') {
			thePseudoMoney -= myCost;
			
			if (myEngieCost != 0)
				thePseudoEngineers -= myEngieCost;
			
			if (thePseudoMoney < 0)
				dial.insufficientFunds();
			else if (thePseudoEngineers < 0)
				dial.insufficientEngineers();
			else {
				money -= myCost;

				switch(myPlane){
					case "F-22 Raptor":
						researchingPlane[0] = true;
						break;
					case "F-35A Lightning II":
						researchingPlane[1] = true;
						break;
					case "F-15EX":
						researchingPlane[2] = true;
						break;
					case "Chengdu J-20":
						researchingPlane[3] = true;
				}

				if (myEngieCost != 0) {
					engineers -= myEngieCost;
					engineersTaken += myEngieCost;
				}
				else {
					engineers += engieAdd;
					temporalWorkers += engieAdd;
					working = true;
				}
				researchedPlane = myPlane;
				
				System.out.println("Your order has been completed");
			}
		}
			
	}

	// Method that displays the trade menu and passes arguments to the "tradeAction" method depending on user input
	public void tradeMenu() {
		Scanner in = new Scanner(System.in);
		int userChoice;
		
		System.out.println("<< Trading >>");
		System.out.println("Please choose what you would like to trade");
		System.out.printf("%-35s", "1. Recruit engineers from China");
		System.out.printf("%-35s %n", "2. Recruit engineers from Israel");
		
		userChoice = in.nextInt();
		in.nextLine();
		
		switch (userChoice) {
		case 1:
			tradeAction("China", 500, 800);
			break;
		case 2:
			tradeAction("Israel", 800, 1200);
			break;
		}
	}

	// Method that displays user choice for trading and calls the "buyAction" method if confirmed.
	public void tradeAction(String engieCountry, int price, int engieHired) {
		System.out.println("You have selected to temporarily hire engineers from " + engieCountry + " which will cost you $" + price);
		System.out.println("(M) and will provide you with " + engieHired + " engineers that will work for this month only.");
		System.out.println("Would you like to complete the contract? Please enter (Y/N)");
		
		buyAction(price, 0, engieCountry, engieHired);
	}

	// Method that adds planes on the hangar
	public void planeAddition(String planeType) {
		switch (planeType) {
		case "F-15E Strike Eagle":
			aircraft[0] += 1;
			break;
		case "AC-130W Stinger II":
			aircraft[1] += 1;
			break;
		case "F-16 Fighting Falcon":
			aircraft[2] += 1;
			break;
		case "MQ-1B Predator":
			aircraft[3] += 1;
			break;
		case "F-22 Raptor":
			aircraft[4] += 1;
			break;
		case "F-35A Lightning II":
			aircraft[5] += 1;
			break;
		case "F-15EX":
			aircraft[6] += 1;
			break;
		case "Chengdu J-20":
			aircraft[7] += 1;
		}
	}

	// Method that creates a file and records the progress that was made in the hangar once the time is up
	public void printReport() {
		try {
			File file = new File("report.txt");
			
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("Final management report:");
			pw.println("1. F-15E Strike Eagle: " + aircraft[0]);
			pw.println("2. AC-130W Stinger II:" + aircraft[1]);
			pw.println("3. F-16 Fighting Falcon: " + aircraft[2]);
			pw.println("4. MQ-1B Predator: " + aircraft[3]);
			pw.println("5. F-22 Raptor: " + aircraft[4]);
			pw.println("6. F-35A Lightning II: " + aircraft[5]);
			pw.println("7. F-15EX: " + aircraft[6]);
			pw.println("8. Chengdu J-20: " + aircraft[7]);
			
			pw.print("\n");
			
			pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	// Method that has a small chance of triggering an event in the hangar
	public void accidentChance() {
		Scanner in = new Scanner(System.in);
		
		double chance = Math.random();
		double accident = Math.random();
		
		if (time > 1) {
		if (chance < 0.20) {
			if (accident < 0.50) {
				System.out.println("You have received a faulty package of propellers!, you will have to pay $100 (M) to cover the cost.");
				System.out.println("Press 'enter' to continue.");
				in.nextLine();
				
				money -= 100;
			}
			else {
				System.out.println("Accident! A testing plane crashed into the Hangar injuring 200 engineers and costing 300 (M) in damages");
				System.out.println("Press 'enter' to continue.");
				in.nextLine();
				
				engineers -= 200;
				money -= 300;
			}
		}
		}
	}
}



// Class dealing with dialogue boxes without performing operations
class Dialogue{
	public void Intro(){
		int menuChoice;
		Scanner in = new Scanner(System.in);
		Dialogue inst = new Dialogue();
		
		System.out.println("<< Welcome to the Hangar Management System >>");
		System.out.println("Please pick one of the command options to get started:");
		System.out.println("'1' To get started with the management.");
		System.out.println("'2' To get the instructions of how to use the management system.");
		System.out.println("'3' To quit.");
		
		menuChoice = in.nextInt();
		in.nextLine();
		
		switch (menuChoice) {
			case 1:
				System.out.println("Let us get started");
				break;
			case 2:
				inst.Instructions();
				break;
			default:
				System.exit(0);
		}
	}
	
	public void resourceStatus(double m, int e, int t) {
		System.out.println("[[ You have " + t + " months left as the hangar manager until you get your service report. ]]");
		System.out.print("You have $" + m + " funds (in millions) at your disposal and " + e);
		System.out.println(" engineers standing by.");
	}
	
	public void Instructions() {
		Scanner in = new Scanner(System.in);
		
		System.out.println("Your job is to do use the resources provided to you by the government to make the military ");
		System.out.println("hangar as productive and fruitful as possible in the span of 1 year. The resources in question");
		System.out.println("are funds and engineers.");
		System.out.println("");
		System.out.println("    Funds: Funds cover the cost of production, research of airplanes, and trading. Production of airplanes");
		System.out.println("is the main objective of the hangar, however researching new types of aircrafts and producing those");
		System.out.println("is also important and more rewarding. At times you will find that you will need to make leisure expenses");
		System.out.println("for the staff or to temporarily hire more engineers to get a task done (this comes a bit expensive) for");
		System.out.println("this reason trading exists. Funds are limited and there is no way to get more once spent");
		System.out.println("    Engineers: The engineers are the people working to get the ordered tasks done. They are spent by ordering");
		System.out.println("them to work on a task, but unlike funds, the amount that was chosen to work will eventually become  available");
		System.out.println("to work again once they finish their task. (this does not apply for engineers hired by trading).");
		System.out.println("   Unexpected events: Since the hangar is an area of production and research, unfortunate events have a chance");
		System.out.println("of being triggered, you should be prepared to cover for the potential loss of funds and even engineers in case");
		System.out.println("an accident occurs.");
		
		System.out.println("");
		System.out.println("Press 'enter' to continue to the managing system.");
		
		in.nextLine();
	}
	
	public void insufficientFunds() {
		Scanner in = new Scanner(System.in);
		
		System.out.println("Insufficient funds, this transaction is impossible.");
		System.out.println("Press 'enter' to continue");
		in.nextLine();
	}
	
	public void insufficientEngineers() {
		Scanner in = new Scanner(System.in);
		
		System.out.println("Not enough manpower for this project, transaction is impossible");
		System.out.println("Press 'enter' to continue");
		in.nextLine();
	}
	
	public void showHangar(int [] myAircraft) {
		Scanner in = new Scanner(System.in);
		int sum = 0;
		
		for (int val : myAircraft) {
			sum += val;
		}
		
		if (sum == 0) {
			System.out.println("There are no planes to be found in the hangar yet, time to build some!");
		}
		else {
			System.out.println("<< Hangar Status >>");
			System.out.println("Planes created:");
			System.out.printf("%-30s", "1. F-15E Strike Eagle: " + myAircraft[0]);
			System.out.printf("%-30s %n", "2. AC-130W Stinger II: " + myAircraft[1]);
			System.out.printf("%-30s", "3. F-16 Fighting Falcon: " + myAircraft[2]);
			System.out.printf("%-30s %n", "4. MQ-1B Predator: " + myAircraft[3]);
			System.out.printf("%-30s", "5. F-22 Raptor: " + myAircraft[4]);
			System.out.printf("%-30s %n", "6. F-35A Lightning II: " + myAircraft[5]);
			System.out.printf("%-30s", "7. F-15EX: " + myAircraft[6]);
			System.out.printf("%-30s %n", "8. Chengdu J-20: " + myAircraft[7]);
		}
		
		System.out.println("Press 'enter' to continue.");
		in.nextLine();
	}

}
