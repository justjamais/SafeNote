<?php

if(!isset($_POST['id']))
  die();

if(empty($_POST['id']))
  die();

  if(!isset($_POST['text']))
  die();


  if(!isset($_POST['username']))
  die();

  if (empty($_POST['username']) || $_POST['username'] =='' || $_POST['username'] == NULL )
  die();


  if(!isset($_POST['password']))
  die();


  if (empty($_POST['password']) || $_POST['password'] == '' || $_POST['password'] == NULL)
  die();



  $hostname='localhost';
  $username='root';
  $password='***************';



try {
    $dbh = new PDO("mysql:host=$hostname;dbname=safenote",$username,$password);
    $sql = "SELECT * FROM  user where username = :username and password = :password;";
    $sth = $dbh->prepare($sql);
    $sth->bindParam(":username", $_POST['username']);
    $sth->bindParam(":password", $_POST['password']);
    $sth->execute();
    $result = $sth->fetchAll(\PDO::FETCH_ASSOC);
    $count =  count($result);
    if($count==1)
    {

      $ilk = $_POST['text'];
      $son = "";
      for($i=0;$i<strlen($ilk);$i++)
      {
        if($ilk[$i]=="\n")
          $son= $son . '\n';
        else
          $son=$son . $ilk[$i];
      }


      $useridd = $result[0]['id'];
      $sql = "UPDATE note SET text = :text  where idnote=:id and userid = :userid;";
    $sth = $dbh->prepare($sql);
    $sth->bindParam(":text", $son);
    $sth->bindParam(":userid", $useridd);
    $sth->bindParam(":id", $_POST['id']);
    if($sth->execute())
    echo "1";
    }
    $dbh = null;

    }
catch(PDOException $e)
    {
    echo $e->getMessage();
    echo "0";
    }

?>
