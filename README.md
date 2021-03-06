# 🔒 SafeChat 
SafeChat is a secure chat that use the system of *Diffie Hellman* and the *AES* algorithm to cipher all the messages storaged in the data base.
To achieve that, you must firstly:

Sign up in the app using an email and a secure password formed by 8 characters at least including:

  -  One Number
  -  One Uppercase character
  -  One Symbol
  
_Sign Up Screen_

![Sign Up Screen](https://github.com/ValeArias07/SafeChat/blob/master/images/login.png)

After that, you can type the email of any of our users and type a pin to start a conversation.
#### Remember! 
>*You can't forget this pin for anything in the world. This pin is unique and it could not be restablished* 

_Session Screen_

![Session Screen](https://github.com/ValeArias07/SafeChat/blob/master/images/session.png)


Now, you can send messages in a safe way to your partner.

_Chat Screen_

![Chat Screen](https://github.com/ValeArias07/SafeChat/blob/master/images/chat.png)

And even if you log out in the session screen, you can login with your email and password!

_Login Screen_

![Login Screen](https://github.com/ValeArias07/SafeChat/blob/master/images/login.png)

## How the *secure cipher* works?
This secure chat uses Diffie Hellman. This means, we need to create a pretty big random prime number called p and a random number call g that you will share with your partner. With this two numbers, we are going to operate you secret pin Sx. 

Once we generate the p and g number, we take them and make, a simple but powerful, operation with your pin Sx:
```
Gx = g ^ Sx
```
We take this number Gx and we make the following operation:

```
Tx = Gx % p
```
This number Tx is going to be storaged in our database. **Dont worry!** It is mathematly **hard** to find your secret pin Sx.

## Now, what's next? 
Your Tx number is going to shared to your partner. In the same way, your partner must give you their Ty number. 
With this number received, we do the next operation:

```
Tyx = (Ty^Sx) % P
```

### 💥BOOM!💥 ###
The Tyx number is going to be your secret password to cipher all the messages in your chat using the AES algorithm. This number is **not gonna be storaged**.

⚠️ That's why you *can't forget your unique pin*⚠️


## How does the AES works?
The AES algorithm that we use in this app is the provided by the class Crypto in Java. This class have a few of methods that allows us to generate an IV, a random array formed by Bytes. Also, this class allows us to generate a password given a word. 

That word is your secret number generated by Diffie Hellman. Remember, you can not forget your secret number.

With this password generated by the class Crypto, all your messages would be encrypt. 
<!-- Write about how we take the number generated in the Diffie Hellman as a password in the AES algorithm -->

## With what kind of problems we had to fight to?
We have to confess it: 
The most difficult part of the app was

> the exchange of the values p,g and vi. These elements has their own classes and convert them properly to storage them in the db was a complete challenge. 

The mechanism of session was a real challenge and we use a few of strategies to save data properly and make  the encrypt and decrypt process


But despite that, we achive the result that we want to 😊. 

## Final comments about this app

The implementation of Diffie Hellman is a powerful tool to storage data in safe way in the developers and enterprises databases.
.
.
.
.
----------


> This app was developed with ❤ by [@Ale](https://github.com/JhonSaldarriaga) & [@Vale](https://github.com/ValeArias07) 




