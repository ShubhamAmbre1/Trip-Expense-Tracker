<?php

require_once '../includes/DbOperations.php';
$response = array();

$username= $_POST['username'];
$name = $_POST['name'];

if($_SERVER['REQUEST_METHOD'] == "POST"){
    if(isset($username)){
        $db = new DbOperations();
        $result = $db->getCurrentTotalExpense($username, $name);
        $response['currentTotalExpense'] = $result['cost'];
    } else {
            $response['error'] = true;
            $response['message'] = "Did not receive all the post requests";
    }
    
} else {
    $response['error'] = true;
    $response['message'] = "Invalid Request";
}
echo json_encode($response);