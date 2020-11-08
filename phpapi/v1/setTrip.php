<?php

require_once '../includes/DbOperations.php';
$response = array();

if($_SERVER['REQUEST_METHOD'] == 'POST'){
    if(isset($_POST['username']) and
        isset($_POST['total_people']) and
        isset($_POST['total_expense']) and
        isset($_POST['loc']) and
        isset($_POST['names'])){

            $db= new DbOperations();
            
            $result = $db->setTotalPeople($_POST['username'],$_POST['total_people'],$_POST['total_expense'],$_POST['loc']);
            
            if($result == 1){
                $response['error'] = false;
                $response['message'] = "Trip details added";

                //foreach ($_POST['names'] as $key => $value){
                 //   $add_names_response = $db->setPeopleNames($_POST['username'], $value['name'])
              //  }
                for($x = 0; $x<sizeof($_POST['names']); $x++){
                    $add_names_response = $db->setPeopleNames($_POST['username'], $_POST['names'][$x]);
                    if($add_names_response== 1){
                        $response['error'] = false;
                        $response['message'] = "Trip successfully started";
                    } else {
                        $response['error'] = true;
                        $response['message'] = "Error adding names";
                    }
                }
                
                
            } else {
                $response['error'] = true;
                $respones['message'] = "Did not receive all the post requests";
            }
        }
} else {
    $response['error'] = true;
    $response['message'] = "Invalid Request";
}

echo json_encode($response);