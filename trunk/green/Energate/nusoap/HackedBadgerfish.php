<?php

/* found this file at:
 * http://decafbad.com/svn/trunk/XoxoOutliner/includes/BadgerFish.php
 */

class HackedBadgerFish {

    public static function map(DOMNode $node, $level = 0) {
        static $xpath;
        if (is_null($xpath)) {
            $xpath = new DOMXPath($node);
        }

        if ($node->childNodes) {
            $r = array();
            $text = '';
            foreach ($node->childNodes as $child) {
                $idx = $child->nodeName;
                // DL remove namspace from tag!
                $idx = preg_replace('/(\w+:)(\w+)/', '$2', $idx);
                if (!is_null($cr = self::map($child, $level + 1))) {
                    if (($child->nodeType == XML_TEXT_NODE) || ($child->nodeType == XML_CDATA_SECTION_NODE)) {
                        $text .= $cr;
                    } else {
                        $r[$idx][] = $cr;
                    }
                }
            }

            // Reduce 1-element numeric arrays
            foreach ($r as $idx => $v) {
                if (is_array($v) && (count($v) == 1) && isset($v[0])) {
                    $r[$idx] = $v[0];
                }
            }

            // Any accumulated text that isn't just whitespace?
            if (strlen(trim($text))) {
                $r['$'] = $text;
            }

            // Attributes?
            $DANSAYSDONOTDOTHIS = false;
            if ($DANSAYSDONOTDOTHIS) {
                if ($node->attributes && $node->attributes->length) {
                    foreach ($node->attributes as $attr) {
                        $r['@' . $attr->nodeName] = $attr->value;
                    }
                }
            }
            // Namespaces?
            // DL
            $DANSAYSDONOTDOTHIS = false;
            if ($DANSAYSDONOTDOTHIS) {

                foreach ($xpath->query('namespace::*[name() != "xml"]', $node) as $ns) {
                    if ($ns->localName == 'xmlns') {
                        $r['@xmlns']['$'] = $ns->namespaceURI;
                    } else {
                        $r['@xmlns'][$ns->localName] = $ns->namespaceURI;
                    }
                }
            }
        }
        // No children -- just return text;
        else {
            if (($node->nodeType == XML_TEXT_NODE) || ($node->nodeType == XML_CDATA_SECTION_NODE)) {
                return $node->textContent;
            }
        }
        if ($level == 0) {
            /*
              $json = new Services_Json();
              $xpath = null;
              return $json->encode($r);
             */
            // DL: if {"Envelope":{"Body":--> return content
            if (isset($r['Envelope'])) {
                $r = $r['Envelope'];
                if (isset($r['Body'])) {
                    $r = $r['Body'];
                }
            }
            // DL: FIXED to use current json...
            // return json_encode($r);
            // actually don't return json at all
            return $r;
        } else {
            // DL : dont do that text as a child thing.
            if (isset($r['$'])) {
                return $r['$'];
            }
            return $r;
        }
    }

}

?>