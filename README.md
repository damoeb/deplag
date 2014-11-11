kalipo
==========================

What is it?
-----------

This a prototype of a plagiarism detector.  It is comes with a HTML frontend to display the potential rip-offs.

The Latest Version
------------------
There is no final version yet. Last Updated Nov 2011.


Documentation
------------
The process of plagiarism detection can be seen as a complex search, like google does. So you have an search index, that holds the relevant data you want to match against and a complex query (e.g. a paper). 

In contrast to a conventianal search, the author of the document that is checked for plagiarisms, will (assume it is acting puckish) try to cover all tracks. Common actions are rephrasing, reordering words and shuffling sentences.



Challenges

False-Positives: coincidatial similar phrases
False-Negatives: rephrasing, translations, reordering



Installation
------------
I am sorry to tell you, the current code does not run. It's been a while..

    mvn install

### Requirements
* git
* java 1.7
* mysql
* mongo db

### Modules
* *deplag-core* index and search
* *deplag-Service* diff ui


Licensing
------------

Please see the file called LICENSE.

Contacts
--------
Markus Ruepp @damoeb
