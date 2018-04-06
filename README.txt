Generic App

Description
	This project aims to create an android application which will allow users to place orders with restaurants, such as crooked cooks, and make payment from it.
	When the user turns on the app for the first time, the app will attempt to read his phone number from the SIM card.
	The user then keys in the table number he is seated at (and number of people if required), upon which he will be directed to the restaurant's menu.
	The user can then select food items, send them to the kitchen, and make an e-payment via Google Pay.
Features
	Automated reading of SIM card phone number
	Forcing the user to the restaurant's menu if the server indicates that he has yet to make a payment
	Allowing the user to exit the restaurant menu if he has yet to make any orders
	ErrorActivity to handle errors such as sudden disruption to WiFi
	Sending of comments along with the orders to indicate food preferences
	Sorting of exisiting orders, such that the most undelivered food items appear first
	Animations for loading images, and transitions between some activities
Future Features
	Allowing the user to input his phone number manually, and sending a OTP verification SMS
	Encrypting the user's phone number after verification
	Integrating PayLah for seamless payment without requiring the user's permission to pay

Authors
	Brian
	Nigel