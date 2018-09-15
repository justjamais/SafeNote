<?php

if(!isset($_GET['username']))
die();

if (empty($_GET['username']) || $_GET['username'] =='' || $_GET['username'] == NULL )
die();


if(!isset($_GET['password']))
die();


if (empty($_GET['password']) || $_GET['password'] == '' || $_GET['password'] == NULL)
die();


if(!isset($_GET['fakepassword']))
die();


if (empty($_GET['fakepassword']) || $_GET['fakepassword'] == '' || $_GET['fakepassword'] == NULL)
die();




$hostname='localhost';
$username='root';
$password='***************';


try {
    $dbh = new PDO("mysql:host=$hostname;dbname=safenote",$username,$password);
    $sql = "SELECT * FROM  user where username = :username";
    $sth = $dbh->prepare($sql);
    $sth->bindParam(":username", $_GET['username']);
    $sth->execute();
    $result = $sth->fetchAll(\PDO::FETCH_ASSOC);
    $count =  count($result);
    if($count==0)
    {
  $sql = "INSERT INTO user(username, password, fakepassword) VALUES(:username, :password, :fakepassword);";
  $sth = $dbh->prepare($sql);
  $sth->bindParam(":username", $_GET['username']);
  $sth->bindParam(":password", $_GET['password']);
  $sth->bindParam(":fakepassword", $_GET['fakepassword']);
  $sth->execute();
  echo '1';
    }

    


    $dbh = null;
    }


catch(PDOException $e)
    {
    echo $e->getMessage();
    echo '0';
    }
 ?>
