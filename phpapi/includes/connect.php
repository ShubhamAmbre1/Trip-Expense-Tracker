<?php

$serverName = "localhost";
$databaseName = "expense_tracker";
$userName = "root";
$password = "";
$conn=mysqli_connect($serverName,$userName,$password,$databaseName);
if(!$conn){
  echo "Error Connecting to Database";
}