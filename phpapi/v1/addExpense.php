<?php

require_once '../includes/DbOperations.php';
$response = array();

$title = $_POST['title'];
$username = $_POST['username'];
$cost = $_POST['cost'];
$name = $_POST['name'];
$image = $_POST['image'];

if($_SERVER['REQUEST_METHOD'] == "POST"){
    if(isset($username) and
        isset($name) and
        isset($cost) and
        isset($image) and 
        isset($title)){

            $filename = $title.".jpg";
            file_put_contents("images/".$filename, base64_decode($image));

            $db = new DbOperations();
            $result = $db->setIndividualExpense($username, $name, $cost, $title);

            if($result == 1){
                $response['error'] = false;
                $response['message'] = "expense and image saved";
            } else {
                $response['error'] = true;
                $response['message'] = "Error while saving error and image";
            }
        } else {
            $response['error'] = true;
            $response['message'] = "Did not receive all the post requests";
        }
} else {
    $response['error'] = true;
    $response['message'] = "Invalid Request";
}

echo json_encode($response);