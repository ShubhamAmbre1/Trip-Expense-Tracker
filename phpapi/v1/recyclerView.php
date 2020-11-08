<?php 

require_once '../includes/connect.php';
$response = array();

$username = $_POST['username'];


$res = mysqli_query($conn, "SELECT * FROM individual_expense"); 
         
while ($row = mysqli_fetch_array($res)){
    $image = file_get_contents("images/".$row[4].".jpg");
    array_push($response, array('name'=>$row[2],
            'cost'=>$row[1],
            'image'=>base64_encode($image)));
}

echo json_encode(array("result"=>$response));

mysqli_close($conn);