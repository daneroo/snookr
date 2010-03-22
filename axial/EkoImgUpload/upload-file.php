<?php
// Change this to allow <yourdomain.com> to make it accessible to your site, or allow <*> for ANYONE to be able to access it.
header('Access-Control: allow <*>');
$uploaddir = './uploads/'; 
$file = $uploaddir . basename($_FILES['uploadfile']['name']); 
 
if (move_uploaded_file($_FILES['uploadfile']['tmp_name'], $file)) { 
  echo "success"; 
} else {
	echo "error";
}
?>