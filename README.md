Library management system.
note: this system will not be deploy online and will only be access via shell cmd and thus have no security built into it and will not have a proper website nor front-end.

1) Basic data
-Books: the BookData hold the initial(id, title, price and publish date) to the 2 main type that is Book and Magazine.
  +Book: have unique attribute of author and genre
  +Magazine: have unique attribute of issue and publisher
These 2 will be the main product of the library. Each of them have a uniquely generated id via UUID.

-Customer: this class hold the information of a customer(Id, name, phone number, email and a check to see if they are a registered member or not)
