<?php
/*
 * Connect to mySQL-Server - Server data are stored in confi.serv.i.php
 * so this file can be updated without breaking anything.
 * v2024
 */

require_once("confi.serv.i.php");
// Default way of connecting with the mySQL database
//In case the connection failes the magic " or EXPRESSION" gets executed,
//with "die( MESSAGE )" to stop further execution.
$sqlcon = mysqli_connect($sqlcon_host,$sqlcon_user,$sqlcon_pass,$sqlcon_dBas) or die("Error ".mysqli_error($sqlcon)." 0");
//We want to tell mySQL that we're using UTF-8, since you might get stuff
//like Umlaute and we want to write/read them correctly
mysqli_set_charset($sqlcon, 'utf8');

// The commonly used result. Don't forget to free it ;)
$sqlresult = null;

/*
 * Following functions wrap the base functions to interact with a database.
 * These mainly exist because the original function names are hell long!
 */


/**
 * After the PHP script is done with all SQL-related code it is good practice
 * to close the database with mysqli_close. It's normaly no problem if you do
 * not close the database as it will be automatically closed after script
 * termination
 * @return	nothing
 */
function sqlDone() {
	global $sqlcon;
	mysqli_close($sqlcon);
}

/**
 * A shortcut for mysqli_real_escape_string(link, string).
 * Nobody got time to type that out every time, right? ;D
 * EVERY INPUT A USER COULD POSSIBLY PUT INTO A QUERY SHOULD EITHER BE
 * ESCAPED OR (NUMERICS) TYPE-CHECKED WITH A CTYPE FUNCTION IN ORDER TO
 * PREVENT SQL-INJECTIONS!
 * @param	$string	The data to be escaped
 * @return	The escaped string
 */
function sqlEscape($string) {
	global $sqlcon;
	return mysqli_real_escape_string($sqlcon, $string);
}

/**
 * In case you want to perform a non-standart query you can just run a raw
 * sql query using this function to get the result in the global
 * $sqlresult. Depending on the type of query the Result-Type will vary,
 * see http://php.net/manual/en/mysqli.query.php
 * EVERY INPUT A USER COULD POSSIBLY PUT INTO A QUERY SHOULD EITHER BE
 * ESCAPED OR (NUMERICS) TYPE-CHECKED WITH A CTYPE FUNCTION IN ORDER TO
 * PREVENT SQL-INJECTIONS!
 * @param	$query	The query to be issued on the database.
 * @return	The global result variable
 */
function sqlQuery($query) {
	global $sqlresult, $sqlcon;
	// echo "<!-- $query -->";
	$sqlresult = mysqli_query($sqlcon, $query);
}

/**
 * This function will free resources occupied by the result of your query.
 * While this is not neccessary it is generally good practice to do so.
 * @return	nothing
 */
function sqlFreeResult() {
	global $sqlresult;
	// Some query only return TRUE on success, any query returns FALSE on failure
	if (is_a($sqlresult, "mysqli_result")) {
		mysqli_free_result($sqlresult);
	}
}
/**
 * This finction will try to return the number of result rows or affected rows.
 * Do NOT use this to obtain the ID for inserted or deleted rows!
 * Normally mysqli_num_rows (or this function) is NOT used, the
 * standart approach is `while (($row=sqlGetRow())!==NULL) ...`
 * @return	The number of affected rows or -1 if not applicable
 */
function sqlRows() {
	global $sqlresult;
	global $sqlcon;

	if (is_a($sqlresult, "mysqli_result")) {
		return mysqli_num_rows($sqlresult);
	} else {
		return mysqli_affected_rows($sqlcon);
	}
}

/**
 * Get the next row from the mySQL result set as associative array,
 * meaning that you can use the column name as index to receive data,
 * or NULL if the cursor is positioned beyond the last result.
 * while ( ( $row = sqlGetRow() ) != NULL )
 *   echo "ID = " . $row[ 'ID' ] ;
 * @return	The next row as array or NULL
 */
function sqlGetRow() {
	global $sqlresult;
	return mysqli_fetch_assoc($sqlresult);
}

/*
 * The next part declares convenience functions for every-day queries.
 * Most finction have a corresponding *SqlQuery* function to build the
 * query, before the query is being executed and the result returned.
 */

/**
 * The most basic function in order to read data from a table.
 * Selects data using the Condition (= WHERE-clause) and additional
 * filters namely ORDER, LIMIT and OFFSET and retuns a result set
 * that can be proccessed with sqlGetRow();
 * Columns should always be enclosed in grave accents (`) in order
 * to prevent confisuion with other syntax elements.
 * If you do not care about a filter e.g. Condition (WHERE-clause) you can
 * skip it by passing NULL as argument. Example use:
 * sqlSelect( 'users', '`Name`, `ID`', NULL, '`ID` DESC', 1 );
 * if ( ( $row = sqlGetRow() ) != NULL )
 *   echo "{$row['Name']} is the newest Member with ID {$row['ID']}" ;
 *
 * For more information on the SELECT syntax see
 * https://dev.mysql.com/doc/refman/5.7/en/select.html
 * @param	$Table		The table name from which you want to read from.
 * 	        The prefix from the config file will automatically be added.
 * @param	$Columns	The name or list of column names to retrieve.
 * 	        The wildcard * for columns means all columns should be returned.
 * 	        If you want to pass a
 * @param	$Condition	If not NULL it will append WHERE $Condition (after columns)
 * @param	$Order		If not NULL it will append ORDER BY $Order (after condition)
 * @param	$Limit		If not NULL it will append LIMIT $Limit (after order)
 * @param	$Offset		If not NULL it will append OFFSET ".$Offset (after limit)
 * @param	$Join		If not NULL it will LEFT JOIN key ON val[0] = val[1] (before condition)
 * @return	$sqlresult as SQL result set or NULL for no results or FALSE in case the query failed
 */
function sqlSelect($Table, $Columns = "*", $Condition = null, $Order = null, $Limit = null, $Offset = null, $Join = null) {
	global $sqlresult;

	//Create query using getSqlQuerySelect and query the query... So descriptive
	sqlQuery(getSqlQuerySelect($Table, $Columns, $Condition, $Order, $Limit, $Offset, $Join).";");

	return $sqlresult;
}

/**
 * Build a SELECT query, see sqlSelect()
 * Table names support "name AS alias", prefix will be applied (and backticks if missing).
 * Collumn names support table prefix, backticks will be applied if missing.
 * @param	$Table		The table name from which you want to read from.
 * 	        The prefix from the config file will automatically be added.
 * @param	$Columns	The name or list of column names to retrieve.
 * 	        The wildcard * for columns means all columns should be returned.
 * @param	$Condition	If not NULL it will append WHERE $Condition (after columns)
 * @param	$Order		If not NULL it will append ORDER BY $Order (after condition)
 * @param	$Limit		If not NULL it will append LIMIT $Limit (after order)
 * @param	$Offset		If not NULL it will append OFFSET ".$Offset (after limit)
 * @param	$Join		If not NULL it will LEFT JOIN tbls ON col1 = col2 (before condition)
 *          Tables are keys in the Array, Collumn associations are values in the Array.
 * 			['tbl2 as t2' => ['t1.a' , 't2.b'] ] <- backticks will be applied if missing.
 * @return	string readied query string
 */
function getSqlQuerySelect($Table, $Columns = "*", $Condition = null, $Order = null, $Limit = null, $Offset = null, $Join = null) {
	// automatically correctly concatinate the array to be a string representation of the columns list
	if (is_array($Columns)) $Columns = colImplode($Columns);
	// build the base query with minimum requirements
	$query = "SELECT ".$Columns." FROM ".sqlExpandTableName($Table);
	// join clause construction is currently only interesting for select
	if ($Join != null) {
		$joinfmt = ' LEFT JOIN ('.implode(', ', array_map('sqlExpandTableName', array_keys($Join))).') ON ';
		$rules = [];
		foreach ($Join as $jtbl => $jcols) {
			if (is_array($jcols)) {
				$rules[] = sqlQuoteColumn($jcols[0]).' = '.sqlQuoteColumn($jcols[1]);
			} else {
				$rules[] = (string)$jcols;
			}
		}
		if (!empty($rules)) $query .= $joinfmt . '('.implode(' AND ', $rules).')';
	}
	// depending on further arguments append filters to the query
	if ($Condition !== null) {
		$part = (is_array($Condition)?kvImplode($Condition, ' AND '):$Condition);
		if (!empty($part)) $query .= " WHERE ".$part;
	}
	if (!empty($Order)) $query .= " ORDER BY ".$Order;
	if (!empty($Limit)) $query .= " LIMIT ".(string)$Limit;
	if (!empty($Offset)) $query .= " OFFSET ".(string)$Offset;

	return $query;
}

/** Inserts a new row into the table.
 * Every column that has no default value or isn't auto increment has to
 * have a value assigned in $Data.
 * The keys are expected to be without spaces and to be NOT enclosed in
 * grave accents. See kvImplode() for information as it's used for $Data.
 * Values are expected to be already escaped before calling this function.
 * @param	$Table	The table to insert a new row into.
 * 	        The prefix from the config file will automatically be added.
 * @param	$Data	An associative array with Key -> Value pairs.
 * @param   $Ignore Ignore duplicate entries
 * @return	The ID the row was inserted with by using mysqli_insert_id
 */
function sqlInsert($Table, $Data, $Ignore=false) {
	global $sqlresult;
	global $sqlcon;

	//Perform created query
	sqlQuery(getSqlQueryInsert($Table, $Data, $Ignore));
	//see php docs fpr mysqli_insert_id
	return mysqli_insert_id($sqlcon);
}

/**
 * Build a INSERT query, see sqlInset()
 * @param	$Table	The table to insert a new row into.
 * 	        The prefix from the config file will automatically be added.
 * @param	$Data	An associative array with Key -> Value pairs.
 * @param   $Ignore Insert ignore into
 * @return	readied query string
 */
function getSqlQueryInsert($Table, $Data, $Ignore=false) {
	//prepare left part
	if ($Ignore)
		$query = "INSERT IGNORE INTO ";
	else
		$query = "INSERT INTO ";
	$query .= sqlExpandTableName($Table)." ";
	//syntax is (KEY, KEY, ...) VALUES (VALUE, VALUE, ...)
	//so we set up both, the key and value list
	$sKeys = implode(', ', array_map('addBackticks', array_keys($Data)));
	$sValues = implode(', ', array_map(fn($x)=>($x === null ? 'NULL' : "'{$x}'"), array_values($Data)));
	$query .= "({$sKeys}) VALUES ({$sValues});"; //finish up query with keys and values
	return $query; //query is now completed and ready to be returned
}

/**
 * Works exactly like sqlInsert but, if a row is found that contains the
 * values for columns given in $Unique the row won't be inserted.
 * For more information on $Data see sqlInsert.
 * Example use would be if you only want to allow a username to be used
 * once throughout your application.
 * $iid = sqlSoftInsert('User', array('Name'=>'Superman', 'EMail'=>'Superman@DailyPlanet.com'), array('Name'=>'Superman'));
 * if ($iid == 0)
 *     echo "A user name Superman already exists";
 * THIS MAY ONLY WORK IF THERE'S AT LEAST ONE ROW IN THE TABLE TO BEGIN WITH
 * @param	$Table	The table to insert a new row into.
 * 	        The prefix from the config file will automatically be added.
 * @param	$Data	An associative array with Key -> Value pairs.
 * @param	$Unique	An associative array with Key -> Value pairs.
 * @return	readied query string
 */
function sqlSoftInsert($Table, $Data, $Unique) {
	global $sqlresult;
	global $sqlcon;

	$query = 'INSERT INTO '.sqlExpandTableName($Table).' ';
	$sKeys = implode(', ', array_map('addBackticks', array_keys($Data)));
	$sValues = implode(', ', array_map(fn($x)=>($x === null ? 'NULL' : "'{$x}'"), array_values($Data)));
	$query .= "({$sKeys}) SELECT {$sValues} FROM DUAL WHERE NOT EXISTS (".
				getSqlQuerySelect($Table, '*', $Unique).') LIMIT 1';
	//And query it
	sqlQuery($query.';');
	return mysqli_insert_id($sqlcon);
}

/**
 * Function that writes the content of $Data in every row matching the
 * condition (= WHERE-clause) and filters for the given table.
 * The keys are expected to be without spaces and to be NOT enclosed in
 * grave accents. See kvImplode() for information as it's used for $Data.
 * If you do not care about a filter e.g. Condition (WHERE-clause) you can
 * skip it by passing NULL as argument. Example:
 * if (sqlUpdate('user', array('`balance`' => '`balance`+50'), "`Name`='Jack'") !== FALSE)
 *   echo "Jack's balance was increased by 50";
 * @param	$Table	The table to insert a new row into.
 * 	        The prefix from the config file will automatically be added.
 * @param	$Data	An associative array with Key -> Value pairs.
 * @param	$Condition	If not NULL it will be appended like $query." WHERE ".$Condition
 * @param	$Order		If not NULL it will be appended like $query." ORDER BY ".$Order
 * @param	$Limit		If not NULL it will be appended like $query." LIMIT ".$Order
 * @return	should be TRUE on success, FALSE otherwise
 */
function sqlUpdate($Table, $Data, $Condition=null, $Order=null, $Limit=null) {
	global $sqlresult;
	sqlQuery(getSqlQueryUpdate($Table, $Data, $Condition, $Order, $Limit));

	return $sqlresult;
}

/**
 * Build UPDATE query, see sqlUpdate()
 * @param	$Table	The table to insert a new row into.
 * 	        The prefix from the config file will automatically be added.
 * @param	$Data	An associative array with Key -> Value pairs.
 * @param	$Condition	If not NULL it will be appended like $query." WHERE ".$Condition
 * @param	$Order		If not NULL it will be appended like $query." ORDER BY ".$Order
 * @param	$Limit		If not NULL it will be appended like $query." LIMIT ".$Order
 * @return	readied query string
 */
function getSqlQueryUpdate($Table, $Data, $Condition=null, $Order=null, $Limit=null) {
	$query = "UPDATE ".($Table !== null ? sqlExpandTableName($Table)." SET " : "");
	$query.=kvImplode($Data, ', ');
	if ($Condition !== null) $query .= " WHERE ".(is_array($Condition)?kvImplode($Condition, ' AND '):$Condition);
	if ($Order !== null) $query .= " ORDER BY ".$Order;
	if ($Limit !== null) $query .= " LIMIT ".(string)$Limit;

	return $query;
}

/** Implements INSER * ON DUPLICATE KEY UPDATE *. Collisions are defined by UNIQUE and PRIMARY keys.
 * @param $Table  The table to insert or update data on.
 *        The prefix from the config file will automatically be added.
 * @param $Data   An associative array with Key -> Value paris.
 * @param $Volatile  Array of colum namess that will be updated on collision. Leave null for all.
 */
function sqlUpsert($Table, $Data, $Volatile=null) {
	global $sqlresult;

	sqlQuery(getSqlQueryUpsert($Table, $Data));

	return $sqlresult;
}

function getSqlQueryUpsert($Table, $Data, $Volatile=null) {
	global $sqltp;
	//prepare left part
	$query = "INSERT INTO ".sqlExpandTableName($Table)." ";
	//syntax is (KEY, KEY, ...) VALUES (VALUE, VALUE, ...)
	//so we set up both, the key and value list
	$sKeys = implode(', ', array_map('addBackticks', array_keys($Data)));
	$sValues = implode(', ', array_map(fn($x)=>"'{$x}'", array_values($Data)));
	//create the filtered data used for the upsert statement
	if ($Volatile === null) {
		$upsert = $Data;
	} else {
		$upsert = [];
		foreach ($Volatile as $key) { $upsert[$key] = $Data[$key]; }
	}
	$query .= "({$sKeys}) VALUES ({$sValues}) ON DUPLICATE KEY UPDATE ".kvImplode($upsert, ', '); //finish up query with keys and values
	return $query; //query is now completed and ready to be returned
}


/** Try to insert a new row, or update if key already existing */
/*/// Deprecated -> See sqlSoftInsert()
function sqlReplaceInto($Table, $Data) {
	global $sqlresult;

	$query = getSqlQueryInsert($Table, $Data)." ON DUPLICATE KEY ".getSqlQueryUpdate(null, $Data, null, null, null);

	sqlQuery($query);

	return $sqlresult;
}
//*///


/**
 * Works similar to the REPLACE statement but does not require the
 * column that's supposed to hold unique data to be primary key.
 * Example: Tables usually have a int ID primary key auto increment column.
 * If you want any EMail to be only used once to be registered in you
 * mailing list REPLACE would not work. With this function you can specify
 * the $Key column to be "EMail", so maybe only the clients name is being
 * updated by this function.
 * For more information on the $Data, see sqlInsert
 * THIS FUNCTION ISSUES TWO QUERIES AS TO LIMITATIONS IN MYSQL
 * @param	$Table	The table to insert a new row into.
 * 	        The prefix from the config file will automatically be added.
 * @param	$Key	The name of the column to be treated as unique key.
 * @param	$Data	An associative array with Key -> Value pairs.
 * @return	should be TRUE on success, FALSE otherwise
 */
function sqlReplace($Table, $Key, $Data) {
	global $sqlresult;
	global $sqlcon;

	// Try to get rows, where the $Key column equals the $Key value in
	// $Data (as that's supposed to be unique)
	$pquery = "SELECT * FROM ".sqlExpandTableName($Table)." WHERE ".$Key."='".$Data[$Key]."' LIMIT 1";
	$privateresult = mysqli_query($sqlcon, $pquery) or die("Error ".mysqli_error($sqlcon)." r");
	// If we have results, ther's already a row with such key->value
	// combination, so we update that
	if ($privateresult && mysqli_num_rows($privateresult)>0) {
		$pquery = getSqlQueryUpdate($Table, $Data, $Key."='".$Data[$Key]."'", null, null);
		mysqli_free_result($privateresult);
	// In case there was no result we can insert a row without creating
	// A duplicate for the $Key column
	} else {
		$pquery = getSqlQueryInsert($Table, $Data);
	}
	//Execute the selected query
	sqlquery($pquery);
	return $sqlresult;
}

/**
 * This function will delete all rows from the selected table when the condition
 * is met. To empty a table it's better to use a TRUNCATE query.
 * @param	$Table	The table to delete rows from.
 * 	        The prefix from the config file will automatically be added.
 * @param	$Condition	If not NULL it will be appended like $query." WHERE ".$Condition
 * @param	$Limit		If not NULL it will be appended like $query." LIMIT ".$Order
 * @return	see the SQL documentations on DELETE FROM
 */
function sqlDelete($Table, $Condition, $Limit = null) {
	global $sqlresult;
	$query = "DELETE FROM ".sqlExpandTableName($Table);
	if ($Condition !== null) $query .= " WHERE ".(is_array($Condition)?kvImplode($Condition, ' AND '):$Condition);
	if ($Limit !== null) $query .= " LIMIT ".(string)$Limit;
	//And query it
	sqlQuery($query);
	return $sqlresult;
}


/** UNUSED
 * Convenience function checking if the argument is a (positive) integer
 */
function isInteger($input){
	return(ctype_digit(strval($input)));
}

function addBackticks($v) {
	if (str_starts_with($v, '`')) return $v;
	return "`$v`";
}

/**
 * Quotes column name parts where needed, table names are not prefixed, 'as' is not supported.
 * Intended for e.g. sqlSelect where Condition is on Joined/Aliased columns, so table prefixed are not applied.
 * a.b -> `a`.`b`
 * a.* -> `a`.*
 */
function sqlQuoteColumn($name) {
	return implode('.', array_map(fn($x)=> (($x == '*') ? $x : addBackticks($x)), explode('.', $name)));
}

/** handles a table definition, adding the global prefix and quoting. aliasing is supported with 't AS a' */
function sqlExpandTableName($tbl) {
	global $sqltp;
	if (($at = stripos($tbl, ' AS '))!== false) {
		return "`$sqltp".substr($tbl, 0, $at)."` AS ".addBackticks(substr($tbl, $at+4));
	} else {
		return "`{$sqltp}{$tbl}`";
	}
}

/**
 * Convenience function to implode associative $Data using a given $Concatinator
 * in SQL manner expecting the keys to be column names formatting a KV-pair like
 *  `KEY`='VALUE'  if VALUE contains ` or  `KEY`=VALUE  otherwise.
 * @param	$Data			An associative array containing the key->value mappings
 * @param	$Concatinator	The string to use to concatinate the KV-pair strings
 * @return	string representation that can be used in many queries
 */
function kvImplode($Data, $Concatinator) {
	if (!is_array($Data)) return $Data;
	$query = [];
	foreach ($Data as $k => $v) {
		if ($v == null) $ev = 'NULL';
		elseif (!empty($v) && (strpos($v,'`')!==FALSE || is_numeric($v))) $ev = $v;
		else $ev = "'$v'";
		$ek = ((str_starts_with($k,'`')) ? $k : sqlQuoteColumn($k));
		$query[] = "$ek=$ev";
	}
	return implode($Concatinator, $query);
}

/** Implode items, putting them in back-ticks for sql columns, if not already in backticks.
 * If items is associative, key=>value will be mapped to "`value` as key" so you get the keys back.
 * column names can be prefixed with database or table name.
 * If $Items is not an array it's assumed to be already prepared and returned as is.
 */
function colImplode($Items) {
	if (!is_array($Items)) return $Items;
	$value = [];
	if (array_is_list($Items)) {
		foreach ($Items as $i) {
			$value[] = addBackticks($i);
		}
	} else {
		foreach ($Items as $k => $v) {
			$ev = sqlQuoteColumn($v);
			$ek = addBackticks($k);
			$value[] = "{$ev} AS {$ek}";
		}
	}
	return implode(', ', $value);
}
