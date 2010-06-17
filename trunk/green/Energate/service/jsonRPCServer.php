<?php
/*
					COPYRIGHT

Copyright 2007 Sergio Vaccaro <sergio@inservibile.org>

This file is part of JSON-RPC PHP.

JSON-RPC PHP is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

JSON-RPC PHP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with JSON-RPC PHP; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * This class build a json-RPC Server 1.0
 * http://json-rpc.org/wiki/specification
 *
 * @author sergio <jsonrpcphp@inservibile.org>
 */
class jsonRPCServer {

    /**
     * This function handle a request binding it to a given object
     *
     * @param object $object
     * @return boolean
     */
    public static function handle($object) {

// checks if a JSON-RCP request has been received
        if ($_SERVER['REQUEST_METHOD'] != 'POST' ||
                empty($_SERVER['CONTENT_TYPE']) ||
//$_SERVER['CONTENT_TYPE'] != 'application/json'
                ! preg_match("/^application\/json/i", $_SERVER['CONTENT_TYPE'])
        ) {
// This is not a JSON-RPC request, maybe a proxy, or test invocation

            if ($_SERVER['REQUEST_METHOD'] == 'GET') {
                jsonRPCServer::showTestPage($object);
                return true;
            }

            return false;
        }

// reads the input data
        $request = json_decode(file_get_contents('php://input'),true);

// executes the task on local object
        try {
            $method = $request['method'];
            try {
                $native =  iM_ReflectionHelper::methodForAliasInClass($object,$method);
                if ($native!=NULL) {
                    $method =  $native;
                }
            } catch (Exception $e) {
            }
            $result = @call_user_func_array(array($object,$method),$request['params']);
// what if the call return FALSE ???
            if ($result !== FALSE) {
                $response = array (
                        'id' => $request['id'],
                        'result' => $result,
                        'error' => NULL
                );
            } else {
                $response = array (
                        'id' => $request['id'],
                        'result' => NULL,
                        'error' => 'unknown method or incorrect parameters'
                );
            }
        } catch (Exception $e) {
            $response = array (
                    'id' => $request['id'],
                    'result' => NULL,
                    'error' => $e->getMessage()
            );
        }

// output the response
        if (!empty($request['id'])) { // notifications don't want response
// JayRock's responses use text/plain, we will use application/json
// JSON-RPC's response type was text/javascript
// $responseContentType ="text/plain; charset=utf-8";
// $responseContentType ="test/javascript";
            $responseContentType = "application/json; charset=utf-8";
            header("Content-Type: ".$responseContentType);
            echo json_encode($response);
        }

// finish
        return true;
    }

    public static function showTestPage($object) {

        $selfurl = $_SERVER['PHP_SELF'];
        $doc = <<<DEMO
        <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"><html><head><title>IntegrationTestService</title><style type="text/css">
                @media screen {
                    body {
                        margin: 0;
                        font-family: arial;
                        font-size: small;
                    }

                    h1 {
                        color: #FFF;
                        font-size: large;
                        padding: 0.5em;
                        background-color: #003366;
                        margin-top: 0;
                    }

                    #Content {
                        margin: 1em;
                    }

                    dt {
                        margin-top: 0.5em;
                    }

                    dd {
                        margin-left: 2.5em;
                    }

                    .method {
                        font-size: small;
                        font-family: Monospace;
                    }

                    .method-name {
                        font-weight: bold;
                        color: #000080;
                    }

                    .method-param {
                        color: #404040;
                    }

                    .obsolete-message {
                        color: #FF0000;
                    }
                }</style></head><body>
                URL: $url
                <div id="Header"><h1>IntegrationTestService</h1></div>
                <div id="Content"><p class="service-help">This is a JSON-RPC service that demonstrates the basic features of the Jayrock library. It also provides a test harness for Energate</p>
                <p class="intro">The following <a href="http://www.json-rpc.org/">JSON-RPC</a> methods are supported (try these using the <a href="#">Some test page</a>):</p><dl><dt class="method"><span class="method-name">describe</span><span class="method-sig"><span class="method-param-open">(</span><span class="method-param">o</span><span class="method-param-close">)</span></span></dt><dd class="method-summary">Returns the type of the passed object, as well as a copy of the object itself.</dd><dt class="method"><span class="method-name">dicoClear</span><span class="method-sig"><span class="method-param-open">(</span><span class="method-param-close">)</span></span></dt><dd class="method-summary">clears dictionary</dd><dt class="method"><span class="method-name">dicoGet</span><span class="method-sig"><span class="method-param-open">(</span><span class="method-param">key</span><span class="method-param-close">)</span></span></dt><dd class="method-summary">puts a key, value into dictionary</dd><dt class="method"><span class="method-name">dicoList</span><span class="method-sig"><span class="method-param-open">(</span><span class="method-param-close">)</span></span></dt><dd class="method-summary">list of keys</dd><dt class="method"><span class="method-name">dicoSet</span><span class="method-sig"><span class="method-param-open">(</span><span class="method-param">key</span><span class="method-param-delim">, </span><span class="method-param">value</span><span class="method-param-close">)</span></span></dt><dd class="method-summary">puts a key, value into dictionary</dd><dt class="method"><span class="method-name">echo</span><span class="method-sig"><span class="method-param-open">(</span><span class="method-param">o</span><span class="method-param-close">)</span></span></dt><dd class="method-summary">Returns the object unmodified</dd><dt class="method"><span class="method-name">reencode</span><span class="method-sig"><span class="method-param-open">(</span><span class="method-param">o</span><span class="method-param-close">)</span></span></dt><dd class="method-summary">Returns the object unmodified, but encoded and decoded on the server</dd><dt class="method"><span class="method-name">system.about</span><span class="method-sig"><span class="method-param-open">(</span><span class="method-param-close">)</span></span></dt><dd class="method-summary">Returns a summary about the server implementation for display purposes.</dd><dt class="method"><span class="method-name">system.getAPI</span><span class="method-sig"><span class="method-param-open">(</span><span class="method-param-close">)</span></span></dt><dd class="method-summary">Returns an Object of method signatures implemented by this service.</dd><dt class="method"><span class="method-name">system.listMethods</span><span class="method-sig"><span class="method-param-open">(</span><span class="method-param-close">)</span></span></dt><dd class="method-summary">Returns an array of method names implemented by this service.</dd><dt class="method"><span class="method-name">system.version</span><span class="method-sig"><span class="method-param-open">(</span><span class="method-param-close">)</span></span></dt><dd class="method-summary">Returns the version server implementation using the major, minor, build and revision format.</dd></dl><hr></hr><p>The following method(s) of this service are marked as idempotent and therefore safe for use with HTTP GET:</p><ul><li>echo</li><li>reencode</li><li>system.about</li><li>system.getAPI</li><li>system.listMethods</li><li>system.version</li></ul></div></body></html>
DEMO;
        echo $doc;
    }

}
?>