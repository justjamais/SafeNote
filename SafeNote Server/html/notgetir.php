<?php


  if(!isset($_GET['username']))
  die();

  if (empty($_GET['username']) || $_GET['username'] =='' || $_GET['username'] == NULL )
  die();


  if(!isset($_GET['password']))
  die();


  if (empty($_GET['password']) || $_GET['password'] == '' || $_GET['password'] == NULL)
  die();




  $hostname='localhost';
  $username='root';
  $password='***************';
try {
    $dbh = new PDO("mysql:host=$hostname;dbname=safenote",$username,$password);
echo "{" . ' "note": [ ';
$i = 0;
    $sql = "SELECT text, idnote FROM  note, user where  username = :username and password = :password and id=note.userid;";
    $sth = $dbh->prepare($sql);
    $sth->bindParam(":username", $_GET['username']);
    $sth->bindParam(":password", $_GET['password']);

    $sth->execute();
    $result = $sth->fetchAll(\PDO::FETCH_ASSOC);
    $count =  count($result);
    for($counter = 0; $counter<$count; $counter++ )
    {
    	if($i++>0)
    		echo ',';
    echo '{ "text":"' . $result[$counter]["text"] .'" , "id": "'. $result[$counter]["idnote"] .'"}';
    }

    echo "] }";
    $dbh = null;
    }
catch(PDOException $e)
    {
    echo $e->getMessage();
    }

?>
