<?php 

    class DbOperations{

        private $con;

        function __construct(){

            require_once dirname(__FILE__).'/DbConnect.php';

            $db = new DbConnect();

            $this->con = $db->connect();
        }

        //User verificaiton and registration
        public function createUser($username, $pass, $email){
            if($this->isUserExist($username, $email)){
                return 0;
            }else{
                $password = md5($pass);
                $stmt = $this->con->prepare("INSERT INTO `users` (`id`, `username`, `password`, `email`) VALUES (NULL, ?, ?, ?)");
                $stmt->bind_param("sss", $username, $password, $email); //three sss for the ??? 

                if($stmt->execute()){
                    return 1;
                }else{
                    return 2;
                }
            }
        }
        
        private function isUserExist($username, $email){
            $stmt = $this->con->prepare("SELECT id FROM users WHERE username = ? OR email = ?");
            $stmt->bind_param("ss", $username, $email);
            $stmt->execute();
            $stmt->store_result();
            return $stmt->num_rows > 0;
        }
        
        public function userLogin($username, $pass){
            $password = md5($pass);
            $stmt = $this->con->prepare("SELECT id FROM users WHERE username = ? AND password = ?");
            $stmt->bind_param("ss", $username, $password);
            $stmt->execute();
            $stmt->store_result();
            return $stmt->num_rows > 0;
        }
        public function getUserByUsername($username){
            $stmt = $this->con->prepare("SELECT * FROM users WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            return $stmt->get_result()->fetch_assoc();
        }
        

        //Total Expense Tracker
        // public function setTotalExpense($username, $expense){
        //     $stmt = $this->con->prepare("UPDATE users SET total = ?, current_trip = true WHERE username = ?");
        //     $stmt->bind_param("??", $expense, $username);
        //     $stmt->execute();
        // }
        // public function getTotalExpense($username){
        //     $stmt = $this->con->prepare("SELECT total_expense FROM users WHERE username = ?");
        //     $stmt->bind_param("?", $username);
        //     $stmt->execute();
        //     return $stmt->get_result()->fetch_assoc();
        // }

        // public function endTrip($username){
        //     $stmt = $this->con->prepare("UPDATE users SET total = 0, current_trip = false WHERE username = ?");
        //     $stmt->bind_param("?", $username);
        //     $stmt->execute();
        // }

        #Starting trip post requests
        public function setTotalPeople($username, $total_people, $total_expense, $loc){
            $stmt = $this->con->prepare("UPDATE users SET current_trip = 1, total_people = ?, total_expense = ?, loc = ? WHERE username = ?");
            $stmt->bind_param("ssss", $total_people, $total_expense, $loc, $username);
            // $stmt->execute();

            if($stmt->execute()){
               return 1;
            } else {
               return 2;
            }
        }

        public function setPeopleNames($username, $name){
            $stmt = $this->con->prepare("INSERT INTO `trip_people` (`person_id`, `username`, `names`) VALUES (NULL, ?, ?);");
            $stmt->bind_param("ss", $username, $name);
            if($stmt->execute()){
                return 1;
            } else {
                return 2;
            }
        }

        //Expense per user and their images
        public function setIndividualExpense($username, $name, $cost, $title){
            $stmt = $this->con->prepare("INSERT INTO `individual_expense` (`id`, `username`, `name`, `cost`, `image`) VALUES (NULL, ?, ?, ?, ?);");
            $stmt->bind_param("ssss", $username, $name, $cost, $title);

            if($stmt->execute()){
                return 1;
            } else {
                return 2;
            }
        }
        //Currently not in use
        public function getIndividualExpense($username){
            $stmt = mysqli_query($this->con, "SELECT * FROM individual_expense WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            return mysqli_fetch_array($stmt->get_result()->fetch_assoc());
        }

        //Get total number of people in group
        public function getTotalPeople($username){
            $stmt = $this->con->prepare("SELECT names FROM trip_people WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            $stmt->store_result();
            return $stmt->num_rows;
        }

        //Get Current Total Expensee
        public function getCurrentTotalExpense($username){
            $stmt = $this->con->prepare("SELECT sum(cost) AS cost FROM individual_expense WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            return $stmt->get_result()->fetch_assoc();
        }

        //Get Individual Total Expense
        public function getIndividualTotalExpense($usernmae, $name){
            $stmt = $this->con->prepare("SELECT sum(cost) FROM individual_expense WHERE username = ? and name = ?");
            $stmt->bind_param("ss", $username, $name);
            $stmt->execute();
            return $stmt->get_result()->fetch_assoc();
        }

        public function isTripActive($username){
            $stmt = $this->con->prepare("SELECT current_trip FROM users WHERE username= ? ");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            return $stmt->get_result()->fetch_assoc();
        }
    }
