<?php

require_once('annotations.php');
class JsonRpcHelp extends Annotation {
    public function  __toString() {
        return "JsonRpcHelp(".$this->value.")";
    }
}
class JsonRpcMethod extends Annotation {
    public function  __toString() {
        return "JsonRpcMethod(".$this->value.")";
    }
}


class iM_ReflectionHelper {
    /* before aliasing */
    public static function nativeMethodsForClass($object) {
        $clazz = new ReflectionClass($object);
        $methods = $clazz->getMethods(ReflectionMethod::IS_PUBLIC);
        $user_methods = array();
        foreach ($methods as $m) {
            $user_methods[] = $m->getName();
        }
        return $user_methods;
    }

    public static function aliasForMethodInClass($object,$nativeName) {
        try {
            $mreflection = new ReflectionAnnotatedMethod($object, $nativeName);
            $alias =  $mreflection->getAnnotation('JsonRpcMethod')->value;
            if ($alias!=NULL) {
                return $alias;
            }
        } catch (Exception $e) {
        }
        return $nativeName;
    }

    /*
     * If a method has been aliased with a JsonRpcMethod with the given alias
     * return the native Metho name for it,
     * otherwise the pased argument is returned unchanges
    */
    public static function methodForAliasInClass($object,$alias) {
        try {
            $native_methods = iM_ReflectionHelper::nativeMethodsForClass($object);
            foreach($native_methods as $nativeMethod) {
                if ($alias==iM_ReflectionHelper::aliasForMethodInClass($object,$nativeMethod)) {
                    return $nativeMethod;
                }
            }
        } catch (Exception $e) {
        }
        // if not found return the alias itself
        return $alias;
    }

}
class iM_ServiceBase {

    /**
     @JsonRpcMethod("system.about")
     @JsonRpcHelp("Returns the json stack name and version")
     */
    public function about() {
        return "JSON-RPC PHP version 1.0";
    }

    /**
     @JsonRpcMethod("system.version")
     @JsonRpcHelp("Returns the json stack version")
     */
    public function version() {
        return "1.0";
    }

    /**
     @JsonRpcMethod("system.listMethods")
     @JsonRpcHelp("Returns the sorted list of (aliased) methodNames for the service")
     */
    public function listMethods() {
        $native_methods = iM_ReflectionHelper::nativeMethodsForClass($this);
        $aliased_methods = array();
        foreach ($native_methods as $m) {
            $aliased_methods[] = iM_ReflectionHelper::aliasForMethodInClass($this,$m);
        }
        sort($aliased_methods);
        return $aliased_methods;
    }

    /**
     @JsonRpcMethod("system.getAPI")
     @JsonRpcHelp("Returns the list of method signatures")
     */
    public function getAPI() {
        $clazz = new ReflectionClass($this);
        $methods = $clazz->getMethods(ReflectionMethod::IS_PUBLIC);
        $api = array();
        // Could add documentation, docTag parsing with getDocCOmment is broken, us an array
        //$api['description']=$object->documentation['class'];
        foreach ($methods as $m) {
            //$api[$this->alias($m->getName())] = "".$m;
            $params = array();
            foreach ($m->getParameters() as $p) {
                // could also include parameter.isOptional,defaultValue, Type hinting if available
                $params[] = $p->getName();
            }
            $api[iM_ReflectionHelper::aliasForMethodInClass($this, $m->getName())] = $params;
        }
        return $api;
    }


}
?>