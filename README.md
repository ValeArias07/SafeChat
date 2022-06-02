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


When it's your first time chatting with your partner, he/she will be able to be notify.
<!-- Put image of notify -->
Now, you can send messages in a safe way to your partner.
<!-- Put image of chat -->

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
<!-- Write about how we take the number generated in the Diffie Hellman as a password in the AES algorithm -->

## With what kind of problems we had to fight to?
We have to confess it: 
> The most problematic part of this app was the notification system. 

To use the push notification, we had to import and do a lot of things. But despite that, we achive the result that we want to 😊 . 

## Final comments about this app
.
.
.
.
----------


> This app was developed with ❤ by [@Ale](https://github.com/JhonSaldarriaga) & [@Vale](https://github.com/ValeArias07) 




