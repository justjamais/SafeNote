<?php


if(!isset($_GET['text']))
die();


  if(!isset($_GET['username']))
  die();

  if (empty($_GET['username']) || $_GET['username'] =='' || $_GET['username'] == NULL )
  die();


  if(!isset($_GET['password']))
  die();


  if (empty($_GET['password']) || $_GET['password'] == '' || $_GET['password'] == NULL)
  die();



$hostname='localhost';
$userid='root';
$password='***************';


try {
    $dbh = new PDO("mysql:host=$hostname;dbname=safenote",$userid,$password);
    $sql = "SELECT * FROM  user where username = :username and password = :password;";
    $sth = $dbh->prepare($sql);
    $sth->bindParam(":username", $_GET['username']);
    $sth->bindParam(":password", $_GET['password']);
    $sth->execute();
    $result = $sth->fetchAll(\PDO::FETCH_ASSOC);
    $count =  count($result);
    

    if($count==1)
    {
$useridd = $result[0]['id'];
  $sql = "INSERT INTO note(userid, text) VALUES(:userid, :text);";
  $sth = $dbh->prepare($sql);
  $sth->bindParam(":userid", $useridd);
  $sth->bindParam(":text", $_GET['text']);
  $sth->execute();
  $sql = "SELECT max(idnote) as id from note where userid = :userid;";
  $sth = $dbh->prepare($sql);
  $sth->bindParam(":userid", $useridd);
  $sth->execute();
  $result = $sth->fetchAll(\PDO::FETCH_ASSOC);
  echo $result[0]['id'];
  
   }
    $dbh = null;
    }


catch(PDOException $e)
    {
    echo $e->getMessage();
    echo  "0";
  }
 ?>
