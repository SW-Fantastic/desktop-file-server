/******/ var __webpack_modules__ = ({

/***/ 2:
/***/ ((module) => {



module.exports = balanced;
function balanced(a, b, str) {
  if (a instanceof RegExp) a = maybeMatch(a, str);
  if (b instanceof RegExp) b = maybeMatch(b, str);
  var r = range(a, b, str);
  return r && {
    start: r[0],
    end: r[1],
    pre: str.slice(0, r[0]),
    body: str.slice(r[0] + a.length, r[1]),
    post: str.slice(r[1] + b.length)
  };
}
function maybeMatch(reg, str) {
  var m = str.match(reg);
  return m ? m[0] : null;
}
balanced.range = range;
function range(a, b, str) {
  var begs, beg, left, right, result;
  var ai = str.indexOf(a);
  var bi = str.indexOf(b, ai + 1);
  var i = ai;
  if (ai >= 0 && bi > 0) {
    begs = [];
    left = str.length;
    while (i >= 0 && !result) {
      if (i == ai) {
        begs.push(i);
        ai = str.indexOf(a, i + 1);
      } else if (begs.length == 1) {
        result = [begs.pop(), bi];
      } else {
        beg = begs.pop();
        if (beg < left) {
          left = beg;
          right = bi;
        }
        bi = str.indexOf(b, i + 1);
      }
      i = ai < bi && ai >= 0 ? ai : bi;
    }
    if (begs.length) {
      result = [left, right];
    }
  }
  return result;
}

/***/ }),

/***/ 101:
/***/ (function(module, exports, __webpack_require__) {

/* module decorator */ module = __webpack_require__.nmd(module);
var __WEBPACK_AMD_DEFINE_RESULT__;/*! https://mths.be/base64 v1.0.0 by @mathias | MIT license */
;
(function (root) {
  // Detect free variables `exports`.
  var freeExports =  true && exports;

  // Detect free variable `module`.
  var freeModule =  true && module && module.exports == freeExports && module;

  // Detect free variable `global`, from Node.js or Browserified code, and use
  // it as `root`.
  var freeGlobal = typeof global == 'object' && global;
  if (freeGlobal.global === freeGlobal || freeGlobal.window === freeGlobal) {
    root = freeGlobal;
  }

  /*--------------------------------------------------------------------------*/

  var InvalidCharacterError = function (message) {
    this.message = message;
  };
  InvalidCharacterError.prototype = new Error();
  InvalidCharacterError.prototype.name = 'InvalidCharacterError';
  var error = function (message) {
    // Note: the error messages used throughout this file match those used by
    // the native `atob`/`btoa` implementation in Chromium.
    throw new InvalidCharacterError(message);
  };
  var TABLE = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
  // http://whatwg.org/html/common-microsyntaxes.html#space-character
  var REGEX_SPACE_CHARACTERS = /[\t\n\f\r ]/g;

  // `decode` is designed to be fully compatible with `atob` as described in the
  // HTML Standard. http://whatwg.org/html/webappapis.html#dom-windowbase64-atob
  // The optimized base64-decoding algorithm used is based on @atk’s excellent
  // implementation. https://gist.github.com/atk/1020396
  var decode = function (input) {
    input = String(input).replace(REGEX_SPACE_CHARACTERS, '');
    var length = input.length;
    if (length % 4 == 0) {
      input = input.replace(/==?$/, '');
      length = input.length;
    }
    if (length % 4 == 1 ||
    // http://whatwg.org/C#alphanumeric-ascii-characters
    /[^+a-zA-Z0-9/]/.test(input)) {
      error('Invalid character: the string to be decoded is not correctly encoded.');
    }
    var bitCounter = 0;
    var bitStorage;
    var buffer;
    var output = '';
    var position = -1;
    while (++position < length) {
      buffer = TABLE.indexOf(input.charAt(position));
      bitStorage = bitCounter % 4 ? bitStorage * 64 + buffer : buffer;
      // Unless this is the first of a group of 4 characters…
      if (bitCounter++ % 4) {
        // …convert the first 8 bits to a single ASCII character.
        output += String.fromCharCode(0xFF & bitStorage >> (-2 * bitCounter & 6));
      }
    }
    return output;
  };

  // `encode` is designed to be fully compatible with `btoa` as described in the
  // HTML Standard: http://whatwg.org/html/webappapis.html#dom-windowbase64-btoa
  var encode = function (input) {
    input = String(input);
    if (/[^\0-\xFF]/.test(input)) {
      // Note: no need to special-case astral symbols here, as surrogates are
      // matched, and the input is supposed to only contain ASCII anyway.
      error('The string to be encoded contains characters outside of the ' + 'Latin1 range.');
    }
    var padding = input.length % 3;
    var output = '';
    var position = -1;
    var a;
    var b;
    var c;
    var buffer;
    // Make sure any padding is handled outside of the loop.
    var length = input.length - padding;
    while (++position < length) {
      // Read three bytes, i.e. 24 bits.
      a = input.charCodeAt(position) << 16;
      b = input.charCodeAt(++position) << 8;
      c = input.charCodeAt(++position);
      buffer = a + b + c;
      // Turn the 24 bits into four chunks of 6 bits each, and append the
      // matching character for each of them to the output.
      output += TABLE.charAt(buffer >> 18 & 0x3F) + TABLE.charAt(buffer >> 12 & 0x3F) + TABLE.charAt(buffer >> 6 & 0x3F) + TABLE.charAt(buffer & 0x3F);
    }
    if (padding == 2) {
      a = input.charCodeAt(position) << 8;
      b = input.charCodeAt(++position);
      buffer = a + b;
      output += TABLE.charAt(buffer >> 10) + TABLE.charAt(buffer >> 4 & 0x3F) + TABLE.charAt(buffer << 2 & 0x3F) + '=';
    } else if (padding == 1) {
      buffer = input.charCodeAt(position);
      output += TABLE.charAt(buffer >> 2) + TABLE.charAt(buffer << 4 & 0x3F) + '==';
    }
    return output;
  };
  var base64 = {
    'encode': encode,
    'decode': decode,
    'version': '1.0.0'
  };

  // Some AMD build optimizers, like r.js, check for specific condition patterns
  // like the following:
  if (true) {
    !(__WEBPACK_AMD_DEFINE_RESULT__ = (function () {
      return base64;
    }).call(exports, __webpack_require__, exports, module),
		__WEBPACK_AMD_DEFINE_RESULT__ !== undefined && (module.exports = __WEBPACK_AMD_DEFINE_RESULT__));
  } else { var key; }
})(this);

/***/ }),

/***/ 172:
/***/ ((__unused_webpack_module, exports) => {

var __webpack_unused_export__;


__webpack_unused_export__ = ({
  value: true
});
/*
 * Calculate the byte lengths for utf8 encoded strings.
 */
function byteLength(str) {
  if (!str) {
    return 0;
  }
  str = str.toString();
  var len = str.length;
  for (var i = str.length; i--;) {
    var code = str.charCodeAt(i);
    if (0xdc00 <= code && code <= 0xdfff) {
      i--;
    }
    if (0x7f < code && code <= 0x7ff) {
      len++;
    } else if (0x7ff < code && code <= 0xffff) {
      len += 2;
    }
  }
  return len;
}
exports.d = byteLength;

/***/ }),

/***/ 526:
/***/ ((module) => {

var charenc = {
  // UTF-8 encoding
  utf8: {
    // Convert a string to a byte array
    stringToBytes: function (str) {
      return charenc.bin.stringToBytes(unescape(encodeURIComponent(str)));
    },
    // Convert a byte array to a string
    bytesToString: function (bytes) {
      return decodeURIComponent(escape(charenc.bin.bytesToString(bytes)));
    }
  },
  // Binary encoding
  bin: {
    // Convert a string to a byte array
    stringToBytes: function (str) {
      for (var bytes = [], i = 0; i < str.length; i++) bytes.push(str.charCodeAt(i) & 0xFF);
      return bytes;
    },
    // Convert a byte array to a string
    bytesToString: function (bytes) {
      for (var str = [], i = 0; i < bytes.length; i++) str.push(String.fromCharCode(bytes[i]));
      return str.join('');
    }
  }
};
module.exports = charenc;

/***/ }),

/***/ 298:
/***/ ((module) => {

(function () {
  var base64map = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/',
    crypt = {
      // Bit-wise rotation left
      rotl: function (n, b) {
        return n << b | n >>> 32 - b;
      },
      // Bit-wise rotation right
      rotr: function (n, b) {
        return n << 32 - b | n >>> b;
      },
      // Swap big-endian to little-endian and vice versa
      endian: function (n) {
        // If number given, swap endian
        if (n.constructor == Number) {
          return crypt.rotl(n, 8) & 0x00FF00FF | crypt.rotl(n, 24) & 0xFF00FF00;
        }

        // Else, assume array and swap all items
        for (var i = 0; i < n.length; i++) n[i] = crypt.endian(n[i]);
        return n;
      },
      // Generate an array of any length of random bytes
      randomBytes: function (n) {
        for (var bytes = []; n > 0; n--) bytes.push(Math.floor(Math.random() * 256));
        return bytes;
      },
      // Convert a byte array to big-endian 32-bit words
      bytesToWords: function (bytes) {
        for (var words = [], i = 0, b = 0; i < bytes.length; i++, b += 8) words[b >>> 5] |= bytes[i] << 24 - b % 32;
        return words;
      },
      // Convert big-endian 32-bit words to a byte array
      wordsToBytes: function (words) {
        for (var bytes = [], b = 0; b < words.length * 32; b += 8) bytes.push(words[b >>> 5] >>> 24 - b % 32 & 0xFF);
        return bytes;
      },
      // Convert a byte array to a hex string
      bytesToHex: function (bytes) {
        for (var hex = [], i = 0; i < bytes.length; i++) {
          hex.push((bytes[i] >>> 4).toString(16));
          hex.push((bytes[i] & 0xF).toString(16));
        }
        return hex.join('');
      },
      // Convert a hex string to a byte array
      hexToBytes: function (hex) {
        for (var bytes = [], c = 0; c < hex.length; c += 2) bytes.push(parseInt(hex.substr(c, 2), 16));
        return bytes;
      },
      // Convert a byte array to a base-64 string
      bytesToBase64: function (bytes) {
        for (var base64 = [], i = 0; i < bytes.length; i += 3) {
          var triplet = bytes[i] << 16 | bytes[i + 1] << 8 | bytes[i + 2];
          for (var j = 0; j < 4; j++) if (i * 8 + j * 6 <= bytes.length * 8) base64.push(base64map.charAt(triplet >>> 6 * (3 - j) & 0x3F));else base64.push('=');
        }
        return base64.join('');
      },
      // Convert a base-64 string to a byte array
      base64ToBytes: function (base64) {
        // Remove non-base-64 characters
        base64 = base64.replace(/[^A-Z0-9+\/]/ig, '');
        for (var bytes = [], i = 0, imod4 = 0; i < base64.length; imod4 = ++i % 4) {
          if (imod4 == 0) continue;
          bytes.push((base64map.indexOf(base64.charAt(i - 1)) & Math.pow(2, -2 * imod4 + 8) - 1) << imod4 * 2 | base64map.indexOf(base64.charAt(i)) >>> 6 - imod4 * 2);
        }
        return bytes;
      }
    };
  module.exports = crypt;
})();

/***/ }),

/***/ 635:
/***/ ((module, __unused_webpack_exports, __webpack_require__) => {



const validator = __webpack_require__(31);
const XMLParser = __webpack_require__(338);
const XMLBuilder = __webpack_require__(221);
module.exports = {
  XMLParser: XMLParser,
  XMLValidator: validator,
  XMLBuilder: XMLBuilder
};

/***/ }),

/***/ 705:
/***/ ((__unused_webpack_module, exports) => {



const nameStartChar = ':A-Za-z_\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD';
const nameChar = nameStartChar + '\\-.\\d\\u00B7\\u0300-\\u036F\\u203F-\\u2040';
const nameRegexp = '[' + nameStartChar + '][' + nameChar + ']*';
const regexName = new RegExp('^' + nameRegexp + '$');
const getAllMatches = function (string, regex) {
  const matches = [];
  let match = regex.exec(string);
  while (match) {
    const allmatches = [];
    allmatches.startIndex = regex.lastIndex - match[0].length;
    const len = match.length;
    for (let index = 0; index < len; index++) {
      allmatches.push(match[index]);
    }
    matches.push(allmatches);
    match = regex.exec(string);
  }
  return matches;
};
const isName = function (string) {
  const match = regexName.exec(string);
  return !(match === null || typeof match === 'undefined');
};
exports.isExist = function (v) {
  return typeof v !== 'undefined';
};
exports.isEmptyObject = function (obj) {
  return Object.keys(obj).length === 0;
};

/**
 * Copy all the properties of a into b.
 * @param {*} target
 * @param {*} a
 */
exports.merge = function (target, a, arrayMode) {
  if (a) {
    const keys = Object.keys(a); // will return an array of own properties
    const len = keys.length; //don't make it inline
    for (let i = 0; i < len; i++) {
      if (arrayMode === 'strict') {
        target[keys[i]] = [a[keys[i]]];
      } else {
        target[keys[i]] = a[keys[i]];
      }
    }
  }
};
/* exports.merge =function (b,a){
  return Object.assign(b,a);
} */

exports.getValue = function (v) {
  if (exports.isExist(v)) {
    return v;
  } else {
    return '';
  }
};

// const fakeCall = function(a) {return a;};
// const fakeCallNoReturn = function() {};

exports.isName = isName;
exports.getAllMatches = getAllMatches;
exports.nameRegexp = nameRegexp;

/***/ }),

/***/ 31:
/***/ ((__unused_webpack_module, exports, __webpack_require__) => {



const util = __webpack_require__(705);
const defaultOptions = {
  allowBooleanAttributes: false,
  //A tag can have attributes without any value
  unpairedTags: []
};

//const tagsPattern = new RegExp("<\\/?([\\w:\\-_\.]+)\\s*\/?>","g");
exports.validate = function (xmlData, options) {
  options = Object.assign({}, defaultOptions, options);

  //xmlData = xmlData.replace(/(\r\n|\n|\r)/gm,"");//make it single line
  //xmlData = xmlData.replace(/(^\s*<\?xml.*?\?>)/g,"");//Remove XML starting tag
  //xmlData = xmlData.replace(/(<!DOCTYPE[\s\w\"\.\/\-\:]+(\[.*\])*\s*>)/g,"");//Remove DOCTYPE
  const tags = [];
  let tagFound = false;

  //indicates that the root tag has been closed (aka. depth 0 has been reached)
  let reachedRoot = false;
  if (xmlData[0] === '\ufeff') {
    // check for byte order mark (BOM)
    xmlData = xmlData.substr(1);
  }
  for (let i = 0; i < xmlData.length; i++) {
    if (xmlData[i] === '<' && xmlData[i + 1] === '?') {
      i += 2;
      i = readPI(xmlData, i);
      if (i.err) return i;
    } else if (xmlData[i] === '<') {
      //starting of tag
      //read until you reach to '>' avoiding any '>' in attribute value
      let tagStartPos = i;
      i++;
      if (xmlData[i] === '!') {
        i = readCommentAndCDATA(xmlData, i);
        continue;
      } else {
        let closingTag = false;
        if (xmlData[i] === '/') {
          //closing tag
          closingTag = true;
          i++;
        }
        //read tagname
        let tagName = '';
        for (; i < xmlData.length && xmlData[i] !== '>' && xmlData[i] !== ' ' && xmlData[i] !== '\t' && xmlData[i] !== '\n' && xmlData[i] !== '\r'; i++) {
          tagName += xmlData[i];
        }
        tagName = tagName.trim();
        //console.log(tagName);

        if (tagName[tagName.length - 1] === '/') {
          //self closing tag without attributes
          tagName = tagName.substring(0, tagName.length - 1);
          //continue;
          i--;
        }
        if (!validateTagName(tagName)) {
          let msg;
          if (tagName.trim().length === 0) {
            msg = "Invalid space after '<'.";
          } else {
            msg = "Tag '" + tagName + "' is an invalid name.";
          }
          return getErrorObject('InvalidTag', msg, getLineNumberForPosition(xmlData, i));
        }
        const result = readAttributeStr(xmlData, i);
        if (result === false) {
          return getErrorObject('InvalidAttr', "Attributes for '" + tagName + "' have open quote.", getLineNumberForPosition(xmlData, i));
        }
        let attrStr = result.value;
        i = result.index;
        if (attrStr[attrStr.length - 1] === '/') {
          //self closing tag
          const attrStrStart = i - attrStr.length;
          attrStr = attrStr.substring(0, attrStr.length - 1);
          const isValid = validateAttributeString(attrStr, options);
          if (isValid === true) {
            tagFound = true;
            //continue; //text may presents after self closing tag
          } else {
            //the result from the nested function returns the position of the error within the attribute
            //in order to get the 'true' error line, we need to calculate the position where the attribute begins (i - attrStr.length) and then add the position within the attribute
            //this gives us the absolute index in the entire xml, which we can use to find the line at last
            return getErrorObject(isValid.err.code, isValid.err.msg, getLineNumberForPosition(xmlData, attrStrStart + isValid.err.line));
          }
        } else if (closingTag) {
          if (!result.tagClosed) {
            return getErrorObject('InvalidTag', "Closing tag '" + tagName + "' doesn't have proper closing.", getLineNumberForPosition(xmlData, i));
          } else if (attrStr.trim().length > 0) {
            return getErrorObject('InvalidTag', "Closing tag '" + tagName + "' can't have attributes or invalid starting.", getLineNumberForPosition(xmlData, tagStartPos));
          } else if (tags.length === 0) {
            return getErrorObject('InvalidTag', "Closing tag '" + tagName + "' has not been opened.", getLineNumberForPosition(xmlData, tagStartPos));
          } else {
            const otg = tags.pop();
            if (tagName !== otg.tagName) {
              let openPos = getLineNumberForPosition(xmlData, otg.tagStartPos);
              return getErrorObject('InvalidTag', "Expected closing tag '" + otg.tagName + "' (opened in line " + openPos.line + ", col " + openPos.col + ") instead of closing tag '" + tagName + "'.", getLineNumberForPosition(xmlData, tagStartPos));
            }

            //when there are no more tags, we reached the root level.
            if (tags.length == 0) {
              reachedRoot = true;
            }
          }
        } else {
          const isValid = validateAttributeString(attrStr, options);
          if (isValid !== true) {
            //the result from the nested function returns the position of the error within the attribute
            //in order to get the 'true' error line, we need to calculate the position where the attribute begins (i - attrStr.length) and then add the position within the attribute
            //this gives us the absolute index in the entire xml, which we can use to find the line at last
            return getErrorObject(isValid.err.code, isValid.err.msg, getLineNumberForPosition(xmlData, i - attrStr.length + isValid.err.line));
          }

          //if the root level has been reached before ...
          if (reachedRoot === true) {
            return getErrorObject('InvalidXml', 'Multiple possible root nodes found.', getLineNumberForPosition(xmlData, i));
          } else if (options.unpairedTags.indexOf(tagName) !== -1) {
            //don't push into stack
          } else {
            tags.push({
              tagName,
              tagStartPos
            });
          }
          tagFound = true;
        }

        //skip tag text value
        //It may include comments and CDATA value
        for (i++; i < xmlData.length; i++) {
          if (xmlData[i] === '<') {
            if (xmlData[i + 1] === '!') {
              //comment or CADATA
              i++;
              i = readCommentAndCDATA(xmlData, i);
              continue;
            } else if (xmlData[i + 1] === '?') {
              i = readPI(xmlData, ++i);
              if (i.err) return i;
            } else {
              break;
            }
          } else if (xmlData[i] === '&') {
            const afterAmp = validateAmpersand(xmlData, i);
            if (afterAmp == -1) return getErrorObject('InvalidChar', "char '&' is not expected.", getLineNumberForPosition(xmlData, i));
            i = afterAmp;
          } else {
            if (reachedRoot === true && !isWhiteSpace(xmlData[i])) {
              return getErrorObject('InvalidXml', "Extra text at the end", getLineNumberForPosition(xmlData, i));
            }
          }
        } //end of reading tag text value
        if (xmlData[i] === '<') {
          i--;
        }
      }
    } else {
      if (isWhiteSpace(xmlData[i])) {
        continue;
      }
      return getErrorObject('InvalidChar', "char '" + xmlData[i] + "' is not expected.", getLineNumberForPosition(xmlData, i));
    }
  }
  if (!tagFound) {
    return getErrorObject('InvalidXml', 'Start tag expected.', 1);
  } else if (tags.length == 1) {
    return getErrorObject('InvalidTag', "Unclosed tag '" + tags[0].tagName + "'.", getLineNumberForPosition(xmlData, tags[0].tagStartPos));
  } else if (tags.length > 0) {
    return getErrorObject('InvalidXml', "Invalid '" + JSON.stringify(tags.map(t => t.tagName), null, 4).replace(/\r?\n/g, '') + "' found.", {
      line: 1,
      col: 1
    });
  }
  return true;
};
function isWhiteSpace(char) {
  return char === ' ' || char === '\t' || char === '\n' || char === '\r';
}
/**
 * Read Processing insstructions and skip
 * @param {*} xmlData
 * @param {*} i
 */
function readPI(xmlData, i) {
  const start = i;
  for (; i < xmlData.length; i++) {
    if (xmlData[i] == '?' || xmlData[i] == ' ') {
      //tagname
      const tagname = xmlData.substr(start, i - start);
      if (i > 5 && tagname === 'xml') {
        return getErrorObject('InvalidXml', 'XML declaration allowed only at the start of the document.', getLineNumberForPosition(xmlData, i));
      } else if (xmlData[i] == '?' && xmlData[i + 1] == '>') {
        //check if valid attribut string
        i++;
        break;
      } else {
        continue;
      }
    }
  }
  return i;
}
function readCommentAndCDATA(xmlData, i) {
  if (xmlData.length > i + 5 && xmlData[i + 1] === '-' && xmlData[i + 2] === '-') {
    //comment
    for (i += 3; i < xmlData.length; i++) {
      if (xmlData[i] === '-' && xmlData[i + 1] === '-' && xmlData[i + 2] === '>') {
        i += 2;
        break;
      }
    }
  } else if (xmlData.length > i + 8 && xmlData[i + 1] === 'D' && xmlData[i + 2] === 'O' && xmlData[i + 3] === 'C' && xmlData[i + 4] === 'T' && xmlData[i + 5] === 'Y' && xmlData[i + 6] === 'P' && xmlData[i + 7] === 'E') {
    let angleBracketsCount = 1;
    for (i += 8; i < xmlData.length; i++) {
      if (xmlData[i] === '<') {
        angleBracketsCount++;
      } else if (xmlData[i] === '>') {
        angleBracketsCount--;
        if (angleBracketsCount === 0) {
          break;
        }
      }
    }
  } else if (xmlData.length > i + 9 && xmlData[i + 1] === '[' && xmlData[i + 2] === 'C' && xmlData[i + 3] === 'D' && xmlData[i + 4] === 'A' && xmlData[i + 5] === 'T' && xmlData[i + 6] === 'A' && xmlData[i + 7] === '[') {
    for (i += 8; i < xmlData.length; i++) {
      if (xmlData[i] === ']' && xmlData[i + 1] === ']' && xmlData[i + 2] === '>') {
        i += 2;
        break;
      }
    }
  }
  return i;
}
const doubleQuote = '"';
const singleQuote = "'";

/**
 * Keep reading xmlData until '<' is found outside the attribute value.
 * @param {string} xmlData
 * @param {number} i
 */
function readAttributeStr(xmlData, i) {
  let attrStr = '';
  let startChar = '';
  let tagClosed = false;
  for (; i < xmlData.length; i++) {
    if (xmlData[i] === doubleQuote || xmlData[i] === singleQuote) {
      if (startChar === '') {
        startChar = xmlData[i];
      } else if (startChar !== xmlData[i]) {
        //if vaue is enclosed with double quote then single quotes are allowed inside the value and vice versa
      } else {
        startChar = '';
      }
    } else if (xmlData[i] === '>') {
      if (startChar === '') {
        tagClosed = true;
        break;
      }
    }
    attrStr += xmlData[i];
  }
  if (startChar !== '') {
    return false;
  }
  return {
    value: attrStr,
    index: i,
    tagClosed: tagClosed
  };
}

/**
 * Select all the attributes whether valid or invalid.
 */
const validAttrStrRegxp = new RegExp('(\\s*)([^\\s=]+)(\\s*=)?(\\s*([\'"])(([\\s\\S])*?)\\5)?', 'g');

//attr, ="sd", a="amit's", a="sd"b="saf", ab  cd=""

function validateAttributeString(attrStr, options) {
  //console.log("start:"+attrStr+":end");

  //if(attrStr.trim().length === 0) return true; //empty string

  const matches = util.getAllMatches(attrStr, validAttrStrRegxp);
  const attrNames = {};
  for (let i = 0; i < matches.length; i++) {
    if (matches[i][1].length === 0) {
      //nospace before attribute name: a="sd"b="saf"
      return getErrorObject('InvalidAttr', "Attribute '" + matches[i][2] + "' has no space in starting.", getPositionFromMatch(matches[i]));
    } else if (matches[i][3] !== undefined && matches[i][4] === undefined) {
      return getErrorObject('InvalidAttr', "Attribute '" + matches[i][2] + "' is without value.", getPositionFromMatch(matches[i]));
    } else if (matches[i][3] === undefined && !options.allowBooleanAttributes) {
      //independent attribute: ab
      return getErrorObject('InvalidAttr', "boolean attribute '" + matches[i][2] + "' is not allowed.", getPositionFromMatch(matches[i]));
    }
    /* else if(matches[i][6] === undefined){//attribute without value: ab=
                    return { err: { code:"InvalidAttr",msg:"attribute " + matches[i][2] + " has no value assigned."}};
                } */
    const attrName = matches[i][2];
    if (!validateAttrName(attrName)) {
      return getErrorObject('InvalidAttr', "Attribute '" + attrName + "' is an invalid name.", getPositionFromMatch(matches[i]));
    }
    if (!attrNames.hasOwnProperty(attrName)) {
      //check for duplicate attribute.
      attrNames[attrName] = 1;
    } else {
      return getErrorObject('InvalidAttr', "Attribute '" + attrName + "' is repeated.", getPositionFromMatch(matches[i]));
    }
  }
  return true;
}
function validateNumberAmpersand(xmlData, i) {
  let re = /\d/;
  if (xmlData[i] === 'x') {
    i++;
    re = /[\da-fA-F]/;
  }
  for (; i < xmlData.length; i++) {
    if (xmlData[i] === ';') return i;
    if (!xmlData[i].match(re)) break;
  }
  return -1;
}
function validateAmpersand(xmlData, i) {
  // https://www.w3.org/TR/xml/#dt-charref
  i++;
  if (xmlData[i] === ';') return -1;
  if (xmlData[i] === '#') {
    i++;
    return validateNumberAmpersand(xmlData, i);
  }
  let count = 0;
  for (; i < xmlData.length; i++, count++) {
    if (xmlData[i].match(/\w/) && count < 20) continue;
    if (xmlData[i] === ';') break;
    return -1;
  }
  return i;
}
function getErrorObject(code, message, lineNumber) {
  return {
    err: {
      code: code,
      msg: message,
      line: lineNumber.line || lineNumber,
      col: lineNumber.col
    }
  };
}
function validateAttrName(attrName) {
  return util.isName(attrName);
}

// const startsWithXML = /^xml/i;

function validateTagName(tagname) {
  return util.isName(tagname) /* && !tagname.match(startsWithXML) */;
}

//this function returns the line number for the character at the given index
function getLineNumberForPosition(xmlData, index) {
  const lines = xmlData.substring(0, index).split(/\r?\n/);
  return {
    line: lines.length,
    // column number is last line's length + 1, because column numbering starts at 1:
    col: lines[lines.length - 1].length + 1
  };
}

//this function returns the position of the first character of match within attrStr
function getPositionFromMatch(match) {
  return match.startIndex + match[1].length;
}

/***/ }),

/***/ 221:
/***/ ((module, __unused_webpack_exports, __webpack_require__) => {



//parse Empty Node as self closing node
const buildFromOrderedJs = __webpack_require__(87);
const defaultOptions = {
  attributeNamePrefix: '@_',
  attributesGroupName: false,
  textNodeName: '#text',
  ignoreAttributes: true,
  cdataPropName: false,
  format: false,
  indentBy: '  ',
  suppressEmptyNode: false,
  suppressUnpairedNode: true,
  suppressBooleanAttributes: true,
  tagValueProcessor: function (key, a) {
    return a;
  },
  attributeValueProcessor: function (attrName, a) {
    return a;
  },
  preserveOrder: false,
  commentPropName: false,
  unpairedTags: [],
  entities: [{
    regex: new RegExp("&", "g"),
    val: "&amp;"
  },
  //it must be on top
  {
    regex: new RegExp(">", "g"),
    val: "&gt;"
  }, {
    regex: new RegExp("<", "g"),
    val: "&lt;"
  }, {
    regex: new RegExp("\'", "g"),
    val: "&apos;"
  }, {
    regex: new RegExp("\"", "g"),
    val: "&quot;"
  }],
  processEntities: true,
  stopNodes: [],
  // transformTagName: false,
  // transformAttributeName: false,
  oneListGroup: false
};
function Builder(options) {
  this.options = Object.assign({}, defaultOptions, options);
  if (this.options.ignoreAttributes || this.options.attributesGroupName) {
    this.isAttribute = function /*a*/
    () {
      return false;
    };
  } else {
    this.attrPrefixLen = this.options.attributeNamePrefix.length;
    this.isAttribute = isAttribute;
  }
  this.processTextOrObjNode = processTextOrObjNode;
  if (this.options.format) {
    this.indentate = indentate;
    this.tagEndChar = '>\n';
    this.newLine = '\n';
  } else {
    this.indentate = function () {
      return '';
    };
    this.tagEndChar = '>';
    this.newLine = '';
  }
}
Builder.prototype.build = function (jObj) {
  if (this.options.preserveOrder) {
    return buildFromOrderedJs(jObj, this.options);
  } else {
    if (Array.isArray(jObj) && this.options.arrayNodeName && this.options.arrayNodeName.length > 1) {
      jObj = {
        [this.options.arrayNodeName]: jObj
      };
    }
    return this.j2x(jObj, 0).val;
  }
};
Builder.prototype.j2x = function (jObj, level) {
  let attrStr = '';
  let val = '';
  for (let key in jObj) {
    if (!Object.prototype.hasOwnProperty.call(jObj, key)) continue;
    if (typeof jObj[key] === 'undefined') {
      // supress undefined node only if it is not an attribute
      if (this.isAttribute(key)) {
        val += '';
      }
    } else if (jObj[key] === null) {
      // null attribute should be ignored by the attribute list, but should not cause the tag closing
      if (this.isAttribute(key)) {
        val += '';
      } else if (key[0] === '?') {
        val += this.indentate(level) + '<' + key + '?' + this.tagEndChar;
      } else {
        val += this.indentate(level) + '<' + key + '/' + this.tagEndChar;
      }
      // val += this.indentate(level) + '<' + key + '/' + this.tagEndChar;
    } else if (jObj[key] instanceof Date) {
      val += this.buildTextValNode(jObj[key], key, '', level);
    } else if (typeof jObj[key] !== 'object') {
      //premitive type
      const attr = this.isAttribute(key);
      if (attr) {
        attrStr += this.buildAttrPairStr(attr, '' + jObj[key]);
      } else {
        //tag value
        if (key === this.options.textNodeName) {
          let newval = this.options.tagValueProcessor(key, '' + jObj[key]);
          val += this.replaceEntitiesValue(newval);
        } else {
          val += this.buildTextValNode(jObj[key], key, '', level);
        }
      }
    } else if (Array.isArray(jObj[key])) {
      //repeated nodes
      const arrLen = jObj[key].length;
      let listTagVal = "";
      let listTagAttr = "";
      for (let j = 0; j < arrLen; j++) {
        const item = jObj[key][j];
        if (typeof item === 'undefined') {
          // supress undefined node
        } else if (item === null) {
          if (key[0] === "?") val += this.indentate(level) + '<' + key + '?' + this.tagEndChar;else val += this.indentate(level) + '<' + key + '/' + this.tagEndChar;
          // val += this.indentate(level) + '<' + key + '/' + this.tagEndChar;
        } else if (typeof item === 'object') {
          if (this.options.oneListGroup) {
            const result = this.j2x(item, level + 1);
            listTagVal += result.val;
            if (this.options.attributesGroupName && item.hasOwnProperty(this.options.attributesGroupName)) {
              listTagAttr += result.attrStr;
            }
          } else {
            listTagVal += this.processTextOrObjNode(item, key, level);
          }
        } else {
          if (this.options.oneListGroup) {
            let textValue = this.options.tagValueProcessor(key, item);
            textValue = this.replaceEntitiesValue(textValue);
            listTagVal += textValue;
          } else {
            listTagVal += this.buildTextValNode(item, key, '', level);
          }
        }
      }
      if (this.options.oneListGroup) {
        listTagVal = this.buildObjectNode(listTagVal, key, listTagAttr, level);
      }
      val += listTagVal;
    } else {
      //nested node
      if (this.options.attributesGroupName && key === this.options.attributesGroupName) {
        const Ks = Object.keys(jObj[key]);
        const L = Ks.length;
        for (let j = 0; j < L; j++) {
          attrStr += this.buildAttrPairStr(Ks[j], '' + jObj[key][Ks[j]]);
        }
      } else {
        val += this.processTextOrObjNode(jObj[key], key, level);
      }
    }
  }
  return {
    attrStr: attrStr,
    val: val
  };
};
Builder.prototype.buildAttrPairStr = function (attrName, val) {
  val = this.options.attributeValueProcessor(attrName, '' + val);
  val = this.replaceEntitiesValue(val);
  if (this.options.suppressBooleanAttributes && val === "true") {
    return ' ' + attrName;
  } else return ' ' + attrName + '="' + val + '"';
};
function processTextOrObjNode(object, key, level) {
  const result = this.j2x(object, level + 1);
  if (object[this.options.textNodeName] !== undefined && Object.keys(object).length === 1) {
    return this.buildTextValNode(object[this.options.textNodeName], key, result.attrStr, level);
  } else {
    return this.buildObjectNode(result.val, key, result.attrStr, level);
  }
}
Builder.prototype.buildObjectNode = function (val, key, attrStr, level) {
  if (val === "") {
    if (key[0] === "?") return this.indentate(level) + '<' + key + attrStr + '?' + this.tagEndChar;else {
      return this.indentate(level) + '<' + key + attrStr + this.closeTag(key) + this.tagEndChar;
    }
  } else {
    let tagEndExp = '</' + key + this.tagEndChar;
    let piClosingChar = "";
    if (key[0] === "?") {
      piClosingChar = "?";
      tagEndExp = "";
    }

    // attrStr is an empty string in case the attribute came as undefined or null
    if ((attrStr || attrStr === '') && val.indexOf('<') === -1) {
      return this.indentate(level) + '<' + key + attrStr + piClosingChar + '>' + val + tagEndExp;
    } else if (this.options.commentPropName !== false && key === this.options.commentPropName && piClosingChar.length === 0) {
      return this.indentate(level) + `<!--${val}-->` + this.newLine;
    } else {
      return this.indentate(level) + '<' + key + attrStr + piClosingChar + this.tagEndChar + val + this.indentate(level) + tagEndExp;
    }
  }
};
Builder.prototype.closeTag = function (key) {
  let closeTag = "";
  if (this.options.unpairedTags.indexOf(key) !== -1) {
    //unpaired
    if (!this.options.suppressUnpairedNode) closeTag = "/";
  } else if (this.options.suppressEmptyNode) {
    //empty
    closeTag = "/";
  } else {
    closeTag = `></${key}`;
  }
  return closeTag;
};
function buildEmptyObjNode(val, key, attrStr, level) {
  if (val !== '') {
    return this.buildObjectNode(val, key, attrStr, level);
  } else {
    if (key[0] === "?") return this.indentate(level) + '<' + key + attrStr + '?' + this.tagEndChar;else {
      return this.indentate(level) + '<' + key + attrStr + '/' + this.tagEndChar;
      // return this.buildTagStr(level,key, attrStr);
    }
  }
}
Builder.prototype.buildTextValNode = function (val, key, attrStr, level) {
  if (this.options.cdataPropName !== false && key === this.options.cdataPropName) {
    return this.indentate(level) + `<![CDATA[${val}]]>` + this.newLine;
  } else if (this.options.commentPropName !== false && key === this.options.commentPropName) {
    return this.indentate(level) + `<!--${val}-->` + this.newLine;
  } else if (key[0] === "?") {
    //PI tag
    return this.indentate(level) + '<' + key + attrStr + '?' + this.tagEndChar;
  } else {
    let textValue = this.options.tagValueProcessor(key, val);
    textValue = this.replaceEntitiesValue(textValue);
    if (textValue === '') {
      return this.indentate(level) + '<' + key + attrStr + this.closeTag(key) + this.tagEndChar;
    } else {
      return this.indentate(level) + '<' + key + attrStr + '>' + textValue + '</' + key + this.tagEndChar;
    }
  }
};
Builder.prototype.replaceEntitiesValue = function (textValue) {
  if (textValue && textValue.length > 0 && this.options.processEntities) {
    for (let i = 0; i < this.options.entities.length; i++) {
      const entity = this.options.entities[i];
      textValue = textValue.replace(entity.regex, entity.val);
    }
  }
  return textValue;
};
function indentate(level) {
  return this.options.indentBy.repeat(level);
}
function isAttribute(name /*, options*/) {
  if (name.startsWith(this.options.attributeNamePrefix) && name !== this.options.textNodeName) {
    return name.substr(this.attrPrefixLen);
  } else {
    return false;
  }
}
module.exports = Builder;

/***/ }),

/***/ 87:
/***/ ((module) => {

const EOL = "\n";

/**
 * 
 * @param {array} jArray 
 * @param {any} options 
 * @returns 
 */
function toXml(jArray, options) {
  let indentation = "";
  if (options.format && options.indentBy.length > 0) {
    indentation = EOL;
  }
  return arrToStr(jArray, options, "", indentation);
}
function arrToStr(arr, options, jPath, indentation) {
  let xmlStr = "";
  let isPreviousElementTag = false;
  for (let i = 0; i < arr.length; i++) {
    const tagObj = arr[i];
    const tagName = propName(tagObj);
    if (tagName === undefined) continue;
    let newJPath = "";
    if (jPath.length === 0) newJPath = tagName;else newJPath = `${jPath}.${tagName}`;
    if (tagName === options.textNodeName) {
      let tagText = tagObj[tagName];
      if (!isStopNode(newJPath, options)) {
        tagText = options.tagValueProcessor(tagName, tagText);
        tagText = replaceEntitiesValue(tagText, options);
      }
      if (isPreviousElementTag) {
        xmlStr += indentation;
      }
      xmlStr += tagText;
      isPreviousElementTag = false;
      continue;
    } else if (tagName === options.cdataPropName) {
      if (isPreviousElementTag) {
        xmlStr += indentation;
      }
      xmlStr += `<![CDATA[${tagObj[tagName][0][options.textNodeName]}]]>`;
      isPreviousElementTag = false;
      continue;
    } else if (tagName === options.commentPropName) {
      xmlStr += indentation + `<!--${tagObj[tagName][0][options.textNodeName]}-->`;
      isPreviousElementTag = true;
      continue;
    } else if (tagName[0] === "?") {
      const attStr = attr_to_str(tagObj[":@"], options);
      const tempInd = tagName === "?xml" ? "" : indentation;
      let piTextNodeName = tagObj[tagName][0][options.textNodeName];
      piTextNodeName = piTextNodeName.length !== 0 ? " " + piTextNodeName : ""; //remove extra spacing
      xmlStr += tempInd + `<${tagName}${piTextNodeName}${attStr}?>`;
      isPreviousElementTag = true;
      continue;
    }
    let newIdentation = indentation;
    if (newIdentation !== "") {
      newIdentation += options.indentBy;
    }
    const attStr = attr_to_str(tagObj[":@"], options);
    const tagStart = indentation + `<${tagName}${attStr}`;
    const tagValue = arrToStr(tagObj[tagName], options, newJPath, newIdentation);
    if (options.unpairedTags.indexOf(tagName) !== -1) {
      if (options.suppressUnpairedNode) xmlStr += tagStart + ">";else xmlStr += tagStart + "/>";
    } else if ((!tagValue || tagValue.length === 0) && options.suppressEmptyNode) {
      xmlStr += tagStart + "/>";
    } else if (tagValue && tagValue.endsWith(">")) {
      xmlStr += tagStart + `>${tagValue}${indentation}</${tagName}>`;
    } else {
      xmlStr += tagStart + ">";
      if (tagValue && indentation !== "" && (tagValue.includes("/>") || tagValue.includes("</"))) {
        xmlStr += indentation + options.indentBy + tagValue + indentation;
      } else {
        xmlStr += tagValue;
      }
      xmlStr += `</${tagName}>`;
    }
    isPreviousElementTag = true;
  }
  return xmlStr;
}
function propName(obj) {
  const keys = Object.keys(obj);
  for (let i = 0; i < keys.length; i++) {
    const key = keys[i];
    if (!obj.hasOwnProperty(key)) continue;
    if (key !== ":@") return key;
  }
}
function attr_to_str(attrMap, options) {
  let attrStr = "";
  if (attrMap && !options.ignoreAttributes) {
    for (let attr in attrMap) {
      if (!attrMap.hasOwnProperty(attr)) continue;
      let attrVal = options.attributeValueProcessor(attr, attrMap[attr]);
      attrVal = replaceEntitiesValue(attrVal, options);
      if (attrVal === true && options.suppressBooleanAttributes) {
        attrStr += ` ${attr.substr(options.attributeNamePrefix.length)}`;
      } else {
        attrStr += ` ${attr.substr(options.attributeNamePrefix.length)}="${attrVal}"`;
      }
    }
  }
  return attrStr;
}
function isStopNode(jPath, options) {
  jPath = jPath.substr(0, jPath.length - options.textNodeName.length - 1);
  let tagName = jPath.substr(jPath.lastIndexOf(".") + 1);
  for (let index in options.stopNodes) {
    if (options.stopNodes[index] === jPath || options.stopNodes[index] === "*." + tagName) return true;
  }
  return false;
}
function replaceEntitiesValue(textValue, options) {
  if (textValue && textValue.length > 0 && options.processEntities) {
    for (let i = 0; i < options.entities.length; i++) {
      const entity = options.entities[i];
      textValue = textValue.replace(entity.regex, entity.val);
    }
  }
  return textValue;
}
module.exports = toXml;

/***/ }),

/***/ 193:
/***/ ((module, __unused_webpack_exports, __webpack_require__) => {

const util = __webpack_require__(705);

//TODO: handle comments
function readDocType(xmlData, i) {
  const entities = {};
  if (xmlData[i + 3] === 'O' && xmlData[i + 4] === 'C' && xmlData[i + 5] === 'T' && xmlData[i + 6] === 'Y' && xmlData[i + 7] === 'P' && xmlData[i + 8] === 'E') {
    i = i + 9;
    let angleBracketsCount = 1;
    let hasBody = false,
      comment = false;
    let exp = "";
    for (; i < xmlData.length; i++) {
      if (xmlData[i] === '<' && !comment) {
        //Determine the tag type
        if (hasBody && isEntity(xmlData, i)) {
          i += 7;
          [entityName, val, i] = readEntityExp(xmlData, i + 1);
          if (val.indexOf("&") === -1)
            //Parameter entities are not supported
            entities[validateEntityName(entityName)] = {
              regx: RegExp(`&${entityName};`, "g"),
              val: val
            };
        } else if (hasBody && isElement(xmlData, i)) i += 8; //Not supported
        else if (hasBody && isAttlist(xmlData, i)) i += 8; //Not supported
        else if (hasBody && isNotation(xmlData, i)) i += 9; //Not supported
        else if (isComment) comment = true;else throw new Error("Invalid DOCTYPE");
        angleBracketsCount++;
        exp = "";
      } else if (xmlData[i] === '>') {
        //Read tag content
        if (comment) {
          if (xmlData[i - 1] === "-" && xmlData[i - 2] === "-") {
            comment = false;
            angleBracketsCount--;
          }
        } else {
          angleBracketsCount--;
        }
        if (angleBracketsCount === 0) {
          break;
        }
      } else if (xmlData[i] === '[') {
        hasBody = true;
      } else {
        exp += xmlData[i];
      }
    }
    if (angleBracketsCount !== 0) {
      throw new Error(`Unclosed DOCTYPE`);
    }
  } else {
    throw new Error(`Invalid Tag instead of DOCTYPE`);
  }
  return {
    entities,
    i
  };
}
function readEntityExp(xmlData, i) {
  //External entities are not supported
  //    <!ENTITY ext SYSTEM "http://normal-website.com" >

  //Parameter entities are not supported
  //    <!ENTITY entityname "&anotherElement;">

  //Internal entities are supported
  //    <!ENTITY entityname "replacement text">

  //read EntityName
  let entityName = "";
  for (; i < xmlData.length && xmlData[i] !== "'" && xmlData[i] !== '"'; i++) {
    // if(xmlData[i] === " ") continue;
    // else 
    entityName += xmlData[i];
  }
  entityName = entityName.trim();
  if (entityName.indexOf(" ") !== -1) throw new Error("External entites are not supported");

  //read Entity Value
  const startChar = xmlData[i++];
  let val = "";
  for (; i < xmlData.length && xmlData[i] !== startChar; i++) {
    val += xmlData[i];
  }
  return [entityName, val, i];
}
function isComment(xmlData, i) {
  if (xmlData[i + 1] === '!' && xmlData[i + 2] === '-' && xmlData[i + 3] === '-') return true;
  return false;
}
function isEntity(xmlData, i) {
  if (xmlData[i + 1] === '!' && xmlData[i + 2] === 'E' && xmlData[i + 3] === 'N' && xmlData[i + 4] === 'T' && xmlData[i + 5] === 'I' && xmlData[i + 6] === 'T' && xmlData[i + 7] === 'Y') return true;
  return false;
}
function isElement(xmlData, i) {
  if (xmlData[i + 1] === '!' && xmlData[i + 2] === 'E' && xmlData[i + 3] === 'L' && xmlData[i + 4] === 'E' && xmlData[i + 5] === 'M' && xmlData[i + 6] === 'E' && xmlData[i + 7] === 'N' && xmlData[i + 8] === 'T') return true;
  return false;
}
function isAttlist(xmlData, i) {
  if (xmlData[i + 1] === '!' && xmlData[i + 2] === 'A' && xmlData[i + 3] === 'T' && xmlData[i + 4] === 'T' && xmlData[i + 5] === 'L' && xmlData[i + 6] === 'I' && xmlData[i + 7] === 'S' && xmlData[i + 8] === 'T') return true;
  return false;
}
function isNotation(xmlData, i) {
  if (xmlData[i + 1] === '!' && xmlData[i + 2] === 'N' && xmlData[i + 3] === 'O' && xmlData[i + 4] === 'T' && xmlData[i + 5] === 'A' && xmlData[i + 6] === 'T' && xmlData[i + 7] === 'I' && xmlData[i + 8] === 'O' && xmlData[i + 9] === 'N') return true;
  return false;
}
function validateEntityName(name) {
  if (util.isName(name)) return name;else throw new Error(`Invalid entity name ${name}`);
}
module.exports = readDocType;

/***/ }),

/***/ 63:
/***/ ((__unused_webpack_module, exports) => {

const defaultOptions = {
  preserveOrder: false,
  attributeNamePrefix: '@_',
  attributesGroupName: false,
  textNodeName: '#text',
  ignoreAttributes: true,
  removeNSPrefix: false,
  // remove NS from tag name or attribute name if true
  allowBooleanAttributes: false,
  //a tag can have attributes without any value
  //ignoreRootElement : false,
  parseTagValue: true,
  parseAttributeValue: false,
  trimValues: true,
  //Trim string values of tag and attributes
  cdataPropName: false,
  numberParseOptions: {
    hex: true,
    leadingZeros: true,
    eNotation: true
  },
  tagValueProcessor: function (tagName, val) {
    return val;
  },
  attributeValueProcessor: function (attrName, val) {
    return val;
  },
  stopNodes: [],
  //nested tags will not be parsed even for errors
  alwaysCreateTextNode: false,
  isArray: () => false,
  commentPropName: false,
  unpairedTags: [],
  processEntities: true,
  htmlEntities: false,
  ignoreDeclaration: false,
  ignorePiTags: false,
  transformTagName: false,
  transformAttributeName: false,
  updateTag: function (tagName, jPath, attrs) {
    return tagName;
  }
  // skipEmptyListItem: false
};
const buildOptions = function (options) {
  return Object.assign({}, defaultOptions, options);
};
exports.buildOptions = buildOptions;
exports.defaultOptions = defaultOptions;

/***/ }),

/***/ 299:
/***/ ((module, __unused_webpack_exports, __webpack_require__) => {



///@ts-check
const util = __webpack_require__(705);
const xmlNode = __webpack_require__(365);
const readDocType = __webpack_require__(193);
const toNumber = __webpack_require__(494);

// const regx =
//   '<((!\\[CDATA\\[([\\s\\S]*?)(]]>))|((NAME:)?(NAME))([^>]*)>|((\\/)(NAME)\\s*>))([^<]*)'
//   .replace(/NAME/g, util.nameRegexp);

//const tagsRegx = new RegExp("<(\\/?[\\w:\\-\._]+)([^>]*)>(\\s*"+cdataRegx+")*([^<]+)?","g");
//const tagsRegx = new RegExp("<(\\/?)((\\w*:)?([\\w:\\-\._]+))([^>]*)>([^<]*)("+cdataRegx+"([^<]*))*([^<]+)?","g");

class OrderedObjParser {
  constructor(options) {
    this.options = options;
    this.currentNode = null;
    this.tagsNodeStack = [];
    this.docTypeEntities = {};
    this.lastEntities = {
      "apos": {
        regex: /&(apos|#39|#x27);/g,
        val: "'"
      },
      "gt": {
        regex: /&(gt|#62|#x3E);/g,
        val: ">"
      },
      "lt": {
        regex: /&(lt|#60|#x3C);/g,
        val: "<"
      },
      "quot": {
        regex: /&(quot|#34|#x22);/g,
        val: "\""
      }
    };
    this.ampEntity = {
      regex: /&(amp|#38|#x26);/g,
      val: "&"
    };
    this.htmlEntities = {
      "space": {
        regex: /&(nbsp|#160);/g,
        val: " "
      },
      // "lt" : { regex: /&(lt|#60);/g, val: "<" },
      // "gt" : { regex: /&(gt|#62);/g, val: ">" },
      // "amp" : { regex: /&(amp|#38);/g, val: "&" },
      // "quot" : { regex: /&(quot|#34);/g, val: "\"" },
      // "apos" : { regex: /&(apos|#39);/g, val: "'" },
      "cent": {
        regex: /&(cent|#162);/g,
        val: "¢"
      },
      "pound": {
        regex: /&(pound|#163);/g,
        val: "£"
      },
      "yen": {
        regex: /&(yen|#165);/g,
        val: "¥"
      },
      "euro": {
        regex: /&(euro|#8364);/g,
        val: "€"
      },
      "copyright": {
        regex: /&(copy|#169);/g,
        val: "©"
      },
      "reg": {
        regex: /&(reg|#174);/g,
        val: "®"
      },
      "inr": {
        regex: /&(inr|#8377);/g,
        val: "₹"
      },
      "num_dec": {
        regex: /&#([0-9]{1,7});/g,
        val: (_, str) => String.fromCharCode(Number.parseInt(str, 10))
      },
      "num_hex": {
        regex: /&#x([0-9a-fA-F]{1,6});/g,
        val: (_, str) => String.fromCharCode(Number.parseInt(str, 16))
      }
    };
    this.addExternalEntities = addExternalEntities;
    this.parseXml = parseXml;
    this.parseTextData = parseTextData;
    this.resolveNameSpace = resolveNameSpace;
    this.buildAttributesMap = buildAttributesMap;
    this.isItStopNode = isItStopNode;
    this.replaceEntitiesValue = replaceEntitiesValue;
    this.readStopNodeData = readStopNodeData;
    this.saveTextToParentTag = saveTextToParentTag;
    this.addChild = addChild;
  }
}
function addExternalEntities(externalEntities) {
  const entKeys = Object.keys(externalEntities);
  for (let i = 0; i < entKeys.length; i++) {
    const ent = entKeys[i];
    this.lastEntities[ent] = {
      regex: new RegExp("&" + ent + ";", "g"),
      val: externalEntities[ent]
    };
  }
}

/**
 * @param {string} val
 * @param {string} tagName
 * @param {string} jPath
 * @param {boolean} dontTrim
 * @param {boolean} hasAttributes
 * @param {boolean} isLeafNode
 * @param {boolean} escapeEntities
 */
function parseTextData(val, tagName, jPath, dontTrim, hasAttributes, isLeafNode, escapeEntities) {
  if (val !== undefined) {
    if (this.options.trimValues && !dontTrim) {
      val = val.trim();
    }
    if (val.length > 0) {
      if (!escapeEntities) val = this.replaceEntitiesValue(val);
      const newval = this.options.tagValueProcessor(tagName, val, jPath, hasAttributes, isLeafNode);
      if (newval === null || newval === undefined) {
        //don't parse
        return val;
      } else if (typeof newval !== typeof val || newval !== val) {
        //overwrite
        return newval;
      } else if (this.options.trimValues) {
        return parseValue(val, this.options.parseTagValue, this.options.numberParseOptions);
      } else {
        const trimmedVal = val.trim();
        if (trimmedVal === val) {
          return parseValue(val, this.options.parseTagValue, this.options.numberParseOptions);
        } else {
          return val;
        }
      }
    }
  }
}
function resolveNameSpace(tagname) {
  if (this.options.removeNSPrefix) {
    const tags = tagname.split(':');
    const prefix = tagname.charAt(0) === '/' ? '/' : '';
    if (tags[0] === 'xmlns') {
      return '';
    }
    if (tags.length === 2) {
      tagname = prefix + tags[1];
    }
  }
  return tagname;
}

//TODO: change regex to capture NS
//const attrsRegx = new RegExp("([\\w\\-\\.\\:]+)\\s*=\\s*(['\"])((.|\n)*?)\\2","gm");
const attrsRegx = new RegExp('([^\\s=]+)\\s*(=\\s*([\'"])([\\s\\S]*?)\\3)?', 'gm');
function buildAttributesMap(attrStr, jPath, tagName) {
  if (!this.options.ignoreAttributes && typeof attrStr === 'string') {
    // attrStr = attrStr.replace(/\r?\n/g, ' ');
    //attrStr = attrStr || attrStr.trim();

    const matches = util.getAllMatches(attrStr, attrsRegx);
    const len = matches.length; //don't make it inline
    const attrs = {};
    for (let i = 0; i < len; i++) {
      const attrName = this.resolveNameSpace(matches[i][1]);
      let oldVal = matches[i][4];
      let aName = this.options.attributeNamePrefix + attrName;
      if (attrName.length) {
        if (this.options.transformAttributeName) {
          aName = this.options.transformAttributeName(aName);
        }
        if (aName === "__proto__") aName = "#__proto__";
        if (oldVal !== undefined) {
          if (this.options.trimValues) {
            oldVal = oldVal.trim();
          }
          oldVal = this.replaceEntitiesValue(oldVal);
          const newVal = this.options.attributeValueProcessor(attrName, oldVal, jPath);
          if (newVal === null || newVal === undefined) {
            //don't parse
            attrs[aName] = oldVal;
          } else if (typeof newVal !== typeof oldVal || newVal !== oldVal) {
            //overwrite
            attrs[aName] = newVal;
          } else {
            //parse
            attrs[aName] = parseValue(oldVal, this.options.parseAttributeValue, this.options.numberParseOptions);
          }
        } else if (this.options.allowBooleanAttributes) {
          attrs[aName] = true;
        }
      }
    }
    if (!Object.keys(attrs).length) {
      return;
    }
    if (this.options.attributesGroupName) {
      const attrCollection = {};
      attrCollection[this.options.attributesGroupName] = attrs;
      return attrCollection;
    }
    return attrs;
  }
}
const parseXml = function (xmlData) {
  xmlData = xmlData.replace(/\r\n?/g, "\n"); //TODO: remove this line
  const xmlObj = new xmlNode('!xml');
  let currentNode = xmlObj;
  let textData = "";
  let jPath = "";
  for (let i = 0; i < xmlData.length; i++) {
    //for each char in XML data
    const ch = xmlData[i];
    if (ch === '<') {
      // const nextIndex = i+1;
      // const _2ndChar = xmlData[nextIndex];
      if (xmlData[i + 1] === '/') {
        //Closing Tag
        const closeIndex = findClosingIndex(xmlData, ">", i, "Closing Tag is not closed.");
        let tagName = xmlData.substring(i + 2, closeIndex).trim();
        if (this.options.removeNSPrefix) {
          const colonIndex = tagName.indexOf(":");
          if (colonIndex !== -1) {
            tagName = tagName.substr(colonIndex + 1);
          }
        }
        if (this.options.transformTagName) {
          tagName = this.options.transformTagName(tagName);
        }
        if (currentNode) {
          textData = this.saveTextToParentTag(textData, currentNode, jPath);
        }

        //check if last tag of nested tag was unpaired tag
        const lastTagName = jPath.substring(jPath.lastIndexOf(".") + 1);
        if (tagName && this.options.unpairedTags.indexOf(tagName) !== -1) {
          throw new Error(`Unpaired tag can not be used as closing tag: </${tagName}>`);
        }
        let propIndex = 0;
        if (lastTagName && this.options.unpairedTags.indexOf(lastTagName) !== -1) {
          propIndex = jPath.lastIndexOf('.', jPath.lastIndexOf('.') - 1);
          this.tagsNodeStack.pop();
        } else {
          propIndex = jPath.lastIndexOf(".");
        }
        jPath = jPath.substring(0, propIndex);
        currentNode = this.tagsNodeStack.pop(); //avoid recursion, set the parent tag scope
        textData = "";
        i = closeIndex;
      } else if (xmlData[i + 1] === '?') {
        let tagData = readTagExp(xmlData, i, false, "?>");
        if (!tagData) throw new Error("Pi Tag is not closed.");
        textData = this.saveTextToParentTag(textData, currentNode, jPath);
        if (this.options.ignoreDeclaration && tagData.tagName === "?xml" || this.options.ignorePiTags) {} else {
          const childNode = new xmlNode(tagData.tagName);
          childNode.add(this.options.textNodeName, "");
          if (tagData.tagName !== tagData.tagExp && tagData.attrExpPresent) {
            childNode[":@"] = this.buildAttributesMap(tagData.tagExp, jPath, tagData.tagName);
          }
          this.addChild(currentNode, childNode, jPath);
        }
        i = tagData.closeIndex + 1;
      } else if (xmlData.substr(i + 1, 3) === '!--') {
        const endIndex = findClosingIndex(xmlData, "-->", i + 4, "Comment is not closed.");
        if (this.options.commentPropName) {
          const comment = xmlData.substring(i + 4, endIndex - 2);
          textData = this.saveTextToParentTag(textData, currentNode, jPath);
          currentNode.add(this.options.commentPropName, [{
            [this.options.textNodeName]: comment
          }]);
        }
        i = endIndex;
      } else if (xmlData.substr(i + 1, 2) === '!D') {
        const result = readDocType(xmlData, i);
        this.docTypeEntities = result.entities;
        i = result.i;
      } else if (xmlData.substr(i + 1, 2) === '![') {
        const closeIndex = findClosingIndex(xmlData, "]]>", i, "CDATA is not closed.") - 2;
        const tagExp = xmlData.substring(i + 9, closeIndex);
        textData = this.saveTextToParentTag(textData, currentNode, jPath);
        let val = this.parseTextData(tagExp, currentNode.tagname, jPath, true, false, true, true);
        if (val == undefined) val = "";

        //cdata should be set even if it is 0 length string
        if (this.options.cdataPropName) {
          currentNode.add(this.options.cdataPropName, [{
            [this.options.textNodeName]: tagExp
          }]);
        } else {
          currentNode.add(this.options.textNodeName, val);
        }
        i = closeIndex + 2;
      } else {
        //Opening tag
        let result = readTagExp(xmlData, i, this.options.removeNSPrefix);
        let tagName = result.tagName;
        const rawTagName = result.rawTagName;
        let tagExp = result.tagExp;
        let attrExpPresent = result.attrExpPresent;
        let closeIndex = result.closeIndex;
        if (this.options.transformTagName) {
          tagName = this.options.transformTagName(tagName);
        }

        //save text as child node
        if (currentNode && textData) {
          if (currentNode.tagname !== '!xml') {
            //when nested tag is found
            textData = this.saveTextToParentTag(textData, currentNode, jPath, false);
          }
        }

        //check if last tag was unpaired tag
        const lastTag = currentNode;
        if (lastTag && this.options.unpairedTags.indexOf(lastTag.tagname) !== -1) {
          currentNode = this.tagsNodeStack.pop();
          jPath = jPath.substring(0, jPath.lastIndexOf("."));
        }
        if (tagName !== xmlObj.tagname) {
          jPath += jPath ? "." + tagName : tagName;
        }
        if (this.isItStopNode(this.options.stopNodes, jPath, tagName)) {
          let tagContent = "";
          //self-closing tag
          if (tagExp.length > 0 && tagExp.lastIndexOf("/") === tagExp.length - 1) {
            if (tagName[tagName.length - 1] === "/") {
              //remove trailing '/'
              tagName = tagName.substr(0, tagName.length - 1);
              jPath = jPath.substr(0, jPath.length - 1);
              tagExp = tagName;
            } else {
              tagExp = tagExp.substr(0, tagExp.length - 1);
            }
            i = result.closeIndex;
          }
          //unpaired tag
          else if (this.options.unpairedTags.indexOf(tagName) !== -1) {
            i = result.closeIndex;
          }
          //normal tag
          else {
            //read until closing tag is found
            const result = this.readStopNodeData(xmlData, rawTagName, closeIndex + 1);
            if (!result) throw new Error(`Unexpected end of ${rawTagName}`);
            i = result.i;
            tagContent = result.tagContent;
          }
          const childNode = new xmlNode(tagName);
          if (tagName !== tagExp && attrExpPresent) {
            childNode[":@"] = this.buildAttributesMap(tagExp, jPath, tagName);
          }
          if (tagContent) {
            tagContent = this.parseTextData(tagContent, tagName, jPath, true, attrExpPresent, true, true);
          }
          jPath = jPath.substr(0, jPath.lastIndexOf("."));
          childNode.add(this.options.textNodeName, tagContent);
          this.addChild(currentNode, childNode, jPath);
        } else {
          //selfClosing tag
          if (tagExp.length > 0 && tagExp.lastIndexOf("/") === tagExp.length - 1) {
            if (tagName[tagName.length - 1] === "/") {
              //remove trailing '/'
              tagName = tagName.substr(0, tagName.length - 1);
              jPath = jPath.substr(0, jPath.length - 1);
              tagExp = tagName;
            } else {
              tagExp = tagExp.substr(0, tagExp.length - 1);
            }
            if (this.options.transformTagName) {
              tagName = this.options.transformTagName(tagName);
            }
            const childNode = new xmlNode(tagName);
            if (tagName !== tagExp && attrExpPresent) {
              childNode[":@"] = this.buildAttributesMap(tagExp, jPath, tagName);
            }
            this.addChild(currentNode, childNode, jPath);
            jPath = jPath.substr(0, jPath.lastIndexOf("."));
          }
          //opening tag
          else {
            const childNode = new xmlNode(tagName);
            this.tagsNodeStack.push(currentNode);
            if (tagName !== tagExp && attrExpPresent) {
              childNode[":@"] = this.buildAttributesMap(tagExp, jPath, tagName);
            }
            this.addChild(currentNode, childNode, jPath);
            currentNode = childNode;
          }
          textData = "";
          i = closeIndex;
        }
      }
    } else {
      textData += xmlData[i];
    }
  }
  return xmlObj.child;
};
function addChild(currentNode, childNode, jPath) {
  const result = this.options.updateTag(childNode.tagname, jPath, childNode[":@"]);
  if (result === false) {} else if (typeof result === "string") {
    childNode.tagname = result;
    currentNode.addChild(childNode);
  } else {
    currentNode.addChild(childNode);
  }
}
const replaceEntitiesValue = function (val) {
  if (this.options.processEntities) {
    for (let entityName in this.docTypeEntities) {
      const entity = this.docTypeEntities[entityName];
      val = val.replace(entity.regx, entity.val);
    }
    for (let entityName in this.lastEntities) {
      const entity = this.lastEntities[entityName];
      val = val.replace(entity.regex, entity.val);
    }
    if (this.options.htmlEntities) {
      for (let entityName in this.htmlEntities) {
        const entity = this.htmlEntities[entityName];
        val = val.replace(entity.regex, entity.val);
      }
    }
    val = val.replace(this.ampEntity.regex, this.ampEntity.val);
  }
  return val;
};
function saveTextToParentTag(textData, currentNode, jPath, isLeafNode) {
  if (textData) {
    //store previously collected data as textNode
    if (isLeafNode === undefined) isLeafNode = Object.keys(currentNode.child).length === 0;
    textData = this.parseTextData(textData, currentNode.tagname, jPath, false, currentNode[":@"] ? Object.keys(currentNode[":@"]).length !== 0 : false, isLeafNode);
    if (textData !== undefined && textData !== "") currentNode.add(this.options.textNodeName, textData);
    textData = "";
  }
  return textData;
}

//TODO: use jPath to simplify the logic
/**
 * 
 * @param {string[]} stopNodes 
 * @param {string} jPath
 * @param {string} currentTagName 
 */
function isItStopNode(stopNodes, jPath, currentTagName) {
  const allNodesExp = "*." + currentTagName;
  for (const stopNodePath in stopNodes) {
    const stopNodeExp = stopNodes[stopNodePath];
    if (allNodesExp === stopNodeExp || jPath === stopNodeExp) return true;
  }
  return false;
}

/**
 * Returns the tag Expression and where it is ending handling single-double quotes situation
 * @param {string} xmlData 
 * @param {number} i starting index
 * @returns 
 */
function tagExpWithClosingIndex(xmlData, i) {
  let closingChar = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : ">";
  let attrBoundary;
  let tagExp = "";
  for (let index = i; index < xmlData.length; index++) {
    let ch = xmlData[index];
    if (attrBoundary) {
      if (ch === attrBoundary) attrBoundary = ""; //reset
    } else if (ch === '"' || ch === "'") {
      attrBoundary = ch;
    } else if (ch === closingChar[0]) {
      if (closingChar[1]) {
        if (xmlData[index + 1] === closingChar[1]) {
          return {
            data: tagExp,
            index: index
          };
        }
      } else {
        return {
          data: tagExp,
          index: index
        };
      }
    } else if (ch === '\t') {
      ch = " ";
    }
    tagExp += ch;
  }
}
function findClosingIndex(xmlData, str, i, errMsg) {
  const closingIndex = xmlData.indexOf(str, i);
  if (closingIndex === -1) {
    throw new Error(errMsg);
  } else {
    return closingIndex + str.length - 1;
  }
}
function readTagExp(xmlData, i, removeNSPrefix) {
  let closingChar = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : ">";
  const result = tagExpWithClosingIndex(xmlData, i + 1, closingChar);
  if (!result) return;
  let tagExp = result.data;
  const closeIndex = result.index;
  const separatorIndex = tagExp.search(/\s/);
  let tagName = tagExp;
  let attrExpPresent = true;
  if (separatorIndex !== -1) {
    //separate tag name and attributes expression
    tagName = tagExp.substring(0, separatorIndex);
    tagExp = tagExp.substring(separatorIndex + 1).trimStart();
  }
  const rawTagName = tagName;
  if (removeNSPrefix) {
    const colonIndex = tagName.indexOf(":");
    if (colonIndex !== -1) {
      tagName = tagName.substr(colonIndex + 1);
      attrExpPresent = tagName !== result.data.substr(colonIndex + 1);
    }
  }
  return {
    tagName: tagName,
    tagExp: tagExp,
    closeIndex: closeIndex,
    attrExpPresent: attrExpPresent,
    rawTagName: rawTagName
  };
}
/**
 * find paired tag for a stop node
 * @param {string} xmlData 
 * @param {string} tagName 
 * @param {number} i 
 */
function readStopNodeData(xmlData, tagName, i) {
  const startIndex = i;
  // Starting at 1 since we already have an open tag
  let openTagCount = 1;
  for (; i < xmlData.length; i++) {
    if (xmlData[i] === "<") {
      if (xmlData[i + 1] === "/") {
        //close tag
        const closeIndex = findClosingIndex(xmlData, ">", i, `${tagName} is not closed`);
        let closeTagName = xmlData.substring(i + 2, closeIndex).trim();
        if (closeTagName === tagName) {
          openTagCount--;
          if (openTagCount === 0) {
            return {
              tagContent: xmlData.substring(startIndex, i),
              i: closeIndex
            };
          }
        }
        i = closeIndex;
      } else if (xmlData[i + 1] === '?') {
        const closeIndex = findClosingIndex(xmlData, "?>", i + 1, "StopNode is not closed.");
        i = closeIndex;
      } else if (xmlData.substr(i + 1, 3) === '!--') {
        const closeIndex = findClosingIndex(xmlData, "-->", i + 3, "StopNode is not closed.");
        i = closeIndex;
      } else if (xmlData.substr(i + 1, 2) === '![') {
        const closeIndex = findClosingIndex(xmlData, "]]>", i, "StopNode is not closed.") - 2;
        i = closeIndex;
      } else {
        const tagData = readTagExp(xmlData, i, '>');
        if (tagData) {
          const openTagName = tagData && tagData.tagName;
          if (openTagName === tagName && tagData.tagExp[tagData.tagExp.length - 1] !== "/") {
            openTagCount++;
          }
          i = tagData.closeIndex;
        }
      }
    }
  } //end for loop
}
function parseValue(val, shouldParse, options) {
  if (shouldParse && typeof val === 'string') {
    //console.log(options)
    const newval = val.trim();
    if (newval === 'true') return true;else if (newval === 'false') return false;else return toNumber(val, options);
  } else {
    if (util.isExist(val)) {
      return val;
    } else {
      return '';
    }
  }
}
module.exports = OrderedObjParser;

/***/ }),

/***/ 338:
/***/ ((module, __unused_webpack_exports, __webpack_require__) => {

const {
  buildOptions
} = __webpack_require__(63);
const OrderedObjParser = __webpack_require__(299);
const {
  prettify
} = __webpack_require__(728);
const validator = __webpack_require__(31);
class XMLParser {
  constructor(options) {
    this.externalEntities = {};
    this.options = buildOptions(options);
  }
  /**
   * Parse XML dats to JS object 
   * @param {string|Buffer} xmlData 
   * @param {boolean|Object} validationOption 
   */
  parse(xmlData, validationOption) {
    if (typeof xmlData === "string") {} else if (xmlData.toString) {
      xmlData = xmlData.toString();
    } else {
      throw new Error("XML data is accepted in String or Bytes[] form.");
    }
    if (validationOption) {
      if (validationOption === true) validationOption = {}; //validate with default options

      const result = validator.validate(xmlData, validationOption);
      if (result !== true) {
        throw Error(`${result.err.msg}:${result.err.line}:${result.err.col}`);
      }
    }
    const orderedObjParser = new OrderedObjParser(this.options);
    orderedObjParser.addExternalEntities(this.externalEntities);
    const orderedResult = orderedObjParser.parseXml(xmlData);
    if (this.options.preserveOrder || orderedResult === undefined) return orderedResult;else return prettify(orderedResult, this.options);
  }

  /**
   * Add Entity which is not by default supported by this library
   * @param {string} key 
   * @param {string} value 
   */
  addEntity(key, value) {
    if (value.indexOf("&") !== -1) {
      throw new Error("Entity value can't have '&'");
    } else if (key.indexOf("&") !== -1 || key.indexOf(";") !== -1) {
      throw new Error("An entity must be set without '&' and ';'. Eg. use '#xD' for '&#xD;'");
    } else if (value === "&") {
      throw new Error("An entity with value '&' is not permitted");
    } else {
      this.externalEntities[key] = value;
    }
  }
}
module.exports = XMLParser;

/***/ }),

/***/ 728:
/***/ ((__unused_webpack_module, exports) => {



/**
 * 
 * @param {array} node 
 * @param {any} options 
 * @returns 
 */
function prettify(node, options) {
  return compress(node, options);
}

/**
 * 
 * @param {array} arr 
 * @param {object} options 
 * @param {string} jPath 
 * @returns object
 */
function compress(arr, options, jPath) {
  let text;
  const compressedObj = {};
  for (let i = 0; i < arr.length; i++) {
    const tagObj = arr[i];
    const property = propName(tagObj);
    let newJpath = "";
    if (jPath === undefined) newJpath = property;else newJpath = jPath + "." + property;
    if (property === options.textNodeName) {
      if (text === undefined) text = tagObj[property];else text += "" + tagObj[property];
    } else if (property === undefined) {
      continue;
    } else if (tagObj[property]) {
      let val = compress(tagObj[property], options, newJpath);
      const isLeaf = isLeafTag(val, options);
      if (tagObj[":@"]) {
        assignAttributes(val, tagObj[":@"], newJpath, options);
      } else if (Object.keys(val).length === 1 && val[options.textNodeName] !== undefined && !options.alwaysCreateTextNode) {
        val = val[options.textNodeName];
      } else if (Object.keys(val).length === 0) {
        if (options.alwaysCreateTextNode) val[options.textNodeName] = "";else val = "";
      }
      if (compressedObj[property] !== undefined && compressedObj.hasOwnProperty(property)) {
        if (!Array.isArray(compressedObj[property])) {
          compressedObj[property] = [compressedObj[property]];
        }
        compressedObj[property].push(val);
      } else {
        //TODO: if a node is not an array, then check if it should be an array
        //also determine if it is a leaf node
        if (options.isArray(property, newJpath, isLeaf)) {
          compressedObj[property] = [val];
        } else {
          compressedObj[property] = val;
        }
      }
    }
  }
  // if(text && text.length > 0) compressedObj[options.textNodeName] = text;
  if (typeof text === "string") {
    if (text.length > 0) compressedObj[options.textNodeName] = text;
  } else if (text !== undefined) compressedObj[options.textNodeName] = text;
  return compressedObj;
}
function propName(obj) {
  const keys = Object.keys(obj);
  for (let i = 0; i < keys.length; i++) {
    const key = keys[i];
    if (key !== ":@") return key;
  }
}
function assignAttributes(obj, attrMap, jpath, options) {
  if (attrMap) {
    const keys = Object.keys(attrMap);
    const len = keys.length; //don't make it inline
    for (let i = 0; i < len; i++) {
      const atrrName = keys[i];
      if (options.isArray(atrrName, jpath + "." + atrrName, true, true)) {
        obj[atrrName] = [attrMap[atrrName]];
      } else {
        obj[atrrName] = attrMap[atrrName];
      }
    }
  }
}
function isLeafTag(obj, options) {
  const {
    textNodeName
  } = options;
  const propCount = Object.keys(obj).length;
  if (propCount === 0) {
    return true;
  }
  if (propCount === 1 && (obj[textNodeName] || typeof obj[textNodeName] === "boolean" || obj[textNodeName] === 0)) {
    return true;
  }
  return false;
}
exports.prettify = prettify;

/***/ }),

/***/ 365:
/***/ ((module) => {



class XmlNode {
  constructor(tagname) {
    this.tagname = tagname;
    this.child = []; //nested tags, text, cdata, comments in order
    this[":@"] = {}; //attributes map
  }
  add(key, val) {
    // this.child.push( {name : key, val: val, isCdata: isCdata });
    if (key === "__proto__") key = "#__proto__";
    this.child.push({
      [key]: val
    });
  }
  addChild(node) {
    if (node.tagname === "__proto__") node.tagname = "#__proto__";
    if (node[":@"] && Object.keys(node[":@"]).length > 0) {
      this.child.push({
        [node.tagname]: node.child,
        [":@"]: node[":@"]
      });
    } else {
      this.child.push({
        [node.tagname]: node.child
      });
    }
  }
}
;
module.exports = XmlNode;

/***/ }),

/***/ 135:
/***/ ((module) => {

/*!
 * Determine if an object is a Buffer
 *
 * @author   Feross Aboukhadijeh <https://feross.org>
 * @license  MIT
 */

// The _isBuffer check is for Safari 5-7 support, because it's missing
// Object.prototype.constructor. Remove this eventually
module.exports = function (obj) {
  return obj != null && (isBuffer(obj) || isSlowBuffer(obj) || !!obj._isBuffer);
};
function isBuffer(obj) {
  return !!obj.constructor && typeof obj.constructor.isBuffer === 'function' && obj.constructor.isBuffer(obj);
}

// For Node v0.10 support. Remove this eventually.
function isSlowBuffer(obj) {
  return typeof obj.readFloatLE === 'function' && typeof obj.slice === 'function' && isBuffer(obj.slice(0, 0));
}

/***/ }),

/***/ 542:
/***/ ((module, __unused_webpack_exports, __webpack_require__) => {

(function () {
  var crypt = __webpack_require__(298),
    utf8 = (__webpack_require__(526).utf8),
    isBuffer = __webpack_require__(135),
    bin = (__webpack_require__(526).bin),
    // The core
    md5 = function (message, options) {
      // Convert to byte array
      if (message.constructor == String) {
        if (options && options.encoding === 'binary') message = bin.stringToBytes(message);else message = utf8.stringToBytes(message);
      } else if (isBuffer(message)) message = Array.prototype.slice.call(message, 0);else if (!Array.isArray(message) && message.constructor !== Uint8Array) message = message.toString();
      // else, assume byte array already

      var m = crypt.bytesToWords(message),
        l = message.length * 8,
        a = 1732584193,
        b = -271733879,
        c = -1732584194,
        d = 271733878;

      // Swap endian
      for (var i = 0; i < m.length; i++) {
        m[i] = (m[i] << 8 | m[i] >>> 24) & 0x00FF00FF | (m[i] << 24 | m[i] >>> 8) & 0xFF00FF00;
      }

      // Padding
      m[l >>> 5] |= 0x80 << l % 32;
      m[(l + 64 >>> 9 << 4) + 14] = l;

      // Method shortcuts
      var FF = md5._ff,
        GG = md5._gg,
        HH = md5._hh,
        II = md5._ii;
      for (var i = 0; i < m.length; i += 16) {
        var aa = a,
          bb = b,
          cc = c,
          dd = d;
        a = FF(a, b, c, d, m[i + 0], 7, -680876936);
        d = FF(d, a, b, c, m[i + 1], 12, -389564586);
        c = FF(c, d, a, b, m[i + 2], 17, 606105819);
        b = FF(b, c, d, a, m[i + 3], 22, -1044525330);
        a = FF(a, b, c, d, m[i + 4], 7, -176418897);
        d = FF(d, a, b, c, m[i + 5], 12, 1200080426);
        c = FF(c, d, a, b, m[i + 6], 17, -1473231341);
        b = FF(b, c, d, a, m[i + 7], 22, -45705983);
        a = FF(a, b, c, d, m[i + 8], 7, 1770035416);
        d = FF(d, a, b, c, m[i + 9], 12, -1958414417);
        c = FF(c, d, a, b, m[i + 10], 17, -42063);
        b = FF(b, c, d, a, m[i + 11], 22, -1990404162);
        a = FF(a, b, c, d, m[i + 12], 7, 1804603682);
        d = FF(d, a, b, c, m[i + 13], 12, -40341101);
        c = FF(c, d, a, b, m[i + 14], 17, -1502002290);
        b = FF(b, c, d, a, m[i + 15], 22, 1236535329);
        a = GG(a, b, c, d, m[i + 1], 5, -165796510);
        d = GG(d, a, b, c, m[i + 6], 9, -1069501632);
        c = GG(c, d, a, b, m[i + 11], 14, 643717713);
        b = GG(b, c, d, a, m[i + 0], 20, -373897302);
        a = GG(a, b, c, d, m[i + 5], 5, -701558691);
        d = GG(d, a, b, c, m[i + 10], 9, 38016083);
        c = GG(c, d, a, b, m[i + 15], 14, -660478335);
        b = GG(b, c, d, a, m[i + 4], 20, -405537848);
        a = GG(a, b, c, d, m[i + 9], 5, 568446438);
        d = GG(d, a, b, c, m[i + 14], 9, -1019803690);
        c = GG(c, d, a, b, m[i + 3], 14, -187363961);
        b = GG(b, c, d, a, m[i + 8], 20, 1163531501);
        a = GG(a, b, c, d, m[i + 13], 5, -1444681467);
        d = GG(d, a, b, c, m[i + 2], 9, -51403784);
        c = GG(c, d, a, b, m[i + 7], 14, 1735328473);
        b = GG(b, c, d, a, m[i + 12], 20, -1926607734);
        a = HH(a, b, c, d, m[i + 5], 4, -378558);
        d = HH(d, a, b, c, m[i + 8], 11, -2022574463);
        c = HH(c, d, a, b, m[i + 11], 16, 1839030562);
        b = HH(b, c, d, a, m[i + 14], 23, -35309556);
        a = HH(a, b, c, d, m[i + 1], 4, -1530992060);
        d = HH(d, a, b, c, m[i + 4], 11, 1272893353);
        c = HH(c, d, a, b, m[i + 7], 16, -155497632);
        b = HH(b, c, d, a, m[i + 10], 23, -1094730640);
        a = HH(a, b, c, d, m[i + 13], 4, 681279174);
        d = HH(d, a, b, c, m[i + 0], 11, -358537222);
        c = HH(c, d, a, b, m[i + 3], 16, -722521979);
        b = HH(b, c, d, a, m[i + 6], 23, 76029189);
        a = HH(a, b, c, d, m[i + 9], 4, -640364487);
        d = HH(d, a, b, c, m[i + 12], 11, -421815835);
        c = HH(c, d, a, b, m[i + 15], 16, 530742520);
        b = HH(b, c, d, a, m[i + 2], 23, -995338651);
        a = II(a, b, c, d, m[i + 0], 6, -198630844);
        d = II(d, a, b, c, m[i + 7], 10, 1126891415);
        c = II(c, d, a, b, m[i + 14], 15, -1416354905);
        b = II(b, c, d, a, m[i + 5], 21, -57434055);
        a = II(a, b, c, d, m[i + 12], 6, 1700485571);
        d = II(d, a, b, c, m[i + 3], 10, -1894986606);
        c = II(c, d, a, b, m[i + 10], 15, -1051523);
        b = II(b, c, d, a, m[i + 1], 21, -2054922799);
        a = II(a, b, c, d, m[i + 8], 6, 1873313359);
        d = II(d, a, b, c, m[i + 15], 10, -30611744);
        c = II(c, d, a, b, m[i + 6], 15, -1560198380);
        b = II(b, c, d, a, m[i + 13], 21, 1309151649);
        a = II(a, b, c, d, m[i + 4], 6, -145523070);
        d = II(d, a, b, c, m[i + 11], 10, -1120210379);
        c = II(c, d, a, b, m[i + 2], 15, 718787259);
        b = II(b, c, d, a, m[i + 9], 21, -343485551);
        a = a + aa >>> 0;
        b = b + bb >>> 0;
        c = c + cc >>> 0;
        d = d + dd >>> 0;
      }
      return crypt.endian([a, b, c, d]);
    };

  // Auxiliary functions
  md5._ff = function (a, b, c, d, x, s, t) {
    var n = a + (b & c | ~b & d) + (x >>> 0) + t;
    return (n << s | n >>> 32 - s) + b;
  };
  md5._gg = function (a, b, c, d, x, s, t) {
    var n = a + (b & d | c & ~d) + (x >>> 0) + t;
    return (n << s | n >>> 32 - s) + b;
  };
  md5._hh = function (a, b, c, d, x, s, t) {
    var n = a + (b ^ c ^ d) + (x >>> 0) + t;
    return (n << s | n >>> 32 - s) + b;
  };
  md5._ii = function (a, b, c, d, x, s, t) {
    var n = a + (c ^ (b | ~d)) + (x >>> 0) + t;
    return (n << s | n >>> 32 - s) + b;
  };

  // Package private blocksize
  md5._blocksize = 16;
  md5._digestsize = 16;
  module.exports = function (message, options) {
    if (message === undefined || message === null) throw new Error('Illegal argument ' + message);
    var digestbytes = crypt.wordsToBytes(md5(message, options));
    return options && options.asBytes ? digestbytes : options && options.asString ? bin.bytesToString(digestbytes) : crypt.bytesToHex(digestbytes);
  };
})();

/***/ }),

/***/ 285:
/***/ ((module, __unused_webpack_exports, __webpack_require__) => {

var balanced = __webpack_require__(2);
module.exports = expandTop;
var escSlash = '\0SLASH' + Math.random() + '\0';
var escOpen = '\0OPEN' + Math.random() + '\0';
var escClose = '\0CLOSE' + Math.random() + '\0';
var escComma = '\0COMMA' + Math.random() + '\0';
var escPeriod = '\0PERIOD' + Math.random() + '\0';
function numeric(str) {
  return parseInt(str, 10) == str ? parseInt(str, 10) : str.charCodeAt(0);
}
function escapeBraces(str) {
  return str.split('\\\\').join(escSlash).split('\\{').join(escOpen).split('\\}').join(escClose).split('\\,').join(escComma).split('\\.').join(escPeriod);
}
function unescapeBraces(str) {
  return str.split(escSlash).join('\\').split(escOpen).join('{').split(escClose).join('}').split(escComma).join(',').split(escPeriod).join('.');
}

// Basically just str.split(","), but handling cases
// where we have nested braced sections, which should be
// treated as individual members, like {a,{b,c},d}
function parseCommaParts(str) {
  if (!str) return [''];
  var parts = [];
  var m = balanced('{', '}', str);
  if (!m) return str.split(',');
  var pre = m.pre;
  var body = m.body;
  var post = m.post;
  var p = pre.split(',');
  p[p.length - 1] += '{' + body + '}';
  var postParts = parseCommaParts(post);
  if (post.length) {
    p[p.length - 1] += postParts.shift();
    p.push.apply(p, postParts);
  }
  parts.push.apply(parts, p);
  return parts;
}
function expandTop(str) {
  if (!str) return [];

  // I don't know why Bash 4.3 does this, but it does.
  // Anything starting with {} will have the first two bytes preserved
  // but *only* at the top level, so {},a}b will not expand to anything,
  // but a{},b}c will be expanded to [a}c,abc].
  // One could argue that this is a bug in Bash, but since the goal of
  // this module is to match Bash's rules, we escape a leading {}
  if (str.substr(0, 2) === '{}') {
    str = '\\{\\}' + str.substr(2);
  }
  return expand(escapeBraces(str), true).map(unescapeBraces);
}
function embrace(str) {
  return '{' + str + '}';
}
function isPadded(el) {
  return /^-?0\d/.test(el);
}
function lte(i, y) {
  return i <= y;
}
function gte(i, y) {
  return i >= y;
}
function expand(str, isTop) {
  var expansions = [];
  var m = balanced('{', '}', str);
  if (!m) return [str];

  // no need to expand pre, since it is guaranteed to be free of brace-sets
  var pre = m.pre;
  var post = m.post.length ? expand(m.post, false) : [''];
  if (/\$$/.test(m.pre)) {
    for (var k = 0; k < post.length; k++) {
      var expansion = pre + '{' + m.body + '}' + post[k];
      expansions.push(expansion);
    }
  } else {
    var isNumericSequence = /^-?\d+\.\.-?\d+(?:\.\.-?\d+)?$/.test(m.body);
    var isAlphaSequence = /^[a-zA-Z]\.\.[a-zA-Z](?:\.\.-?\d+)?$/.test(m.body);
    var isSequence = isNumericSequence || isAlphaSequence;
    var isOptions = m.body.indexOf(',') >= 0;
    if (!isSequence && !isOptions) {
      // {a},b}
      if (m.post.match(/,.*\}/)) {
        str = m.pre + '{' + m.body + escClose + m.post;
        return expand(str);
      }
      return [str];
    }
    var n;
    if (isSequence) {
      n = m.body.split(/\.\./);
    } else {
      n = parseCommaParts(m.body);
      if (n.length === 1) {
        // x{{a,b}}y ==> x{a}y x{b}y
        n = expand(n[0], false).map(embrace);
        if (n.length === 1) {
          return post.map(function (p) {
            return m.pre + n[0] + p;
          });
        }
      }
    }

    // at this point, n is the parts, and we know it's not a comma set
    // with a single entry.
    var N;
    if (isSequence) {
      var x = numeric(n[0]);
      var y = numeric(n[1]);
      var width = Math.max(n[0].length, n[1].length);
      var incr = n.length == 3 ? Math.abs(numeric(n[2])) : 1;
      var test = lte;
      var reverse = y < x;
      if (reverse) {
        incr *= -1;
        test = gte;
      }
      var pad = n.some(isPadded);
      N = [];
      for (var i = x; test(i, y); i += incr) {
        var c;
        if (isAlphaSequence) {
          c = String.fromCharCode(i);
          if (c === '\\') c = '';
        } else {
          c = String(i);
          if (pad) {
            var need = width - c.length;
            if (need > 0) {
              var z = new Array(need + 1).join('0');
              if (i < 0) c = '-' + z + c.slice(1);else c = z + c;
            }
          }
        }
        N.push(c);
      }
    } else {
      N = [];
      for (var j = 0; j < n.length; j++) {
        N.push.apply(N, expand(n[j], false));
      }
    }
    for (var j = 0; j < N.length; j++) {
      for (var k = 0; k < post.length; k++) {
        var expansion = pre + N[j] + post[k];
        if (!isTop || isSequence || expansion) expansions.push(expansion);
      }
    }
  }
  return expansions;
}

/***/ }),

/***/ 829:
/***/ ((module) => {

/**
* @license nested-property https://github.com/cosmosio/nested-property
*
* The MIT License (MIT)
*
* Copyright (c) 2014-2020 Olivier Scherrer <pode.fr@gmail.com>
*/


function _typeof(obj) {
  "@babel/helpers - typeof";

  if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") {
    _typeof = function _typeof(obj) {
      return typeof obj;
    };
  } else {
    _typeof = function _typeof(obj) {
      return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj;
    };
  }
  return _typeof(obj);
}
function _classCallCheck(instance, Constructor) {
  if (!(instance instanceof Constructor)) {
    throw new TypeError("Cannot call a class as a function");
  }
}
function _possibleConstructorReturn(self, call) {
  if (call && (_typeof(call) === "object" || typeof call === "function")) {
    return call;
  }
  return _assertThisInitialized(self);
}
function _assertThisInitialized(self) {
  if (self === void 0) {
    throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
  }
  return self;
}
function _inherits(subClass, superClass) {
  if (typeof superClass !== "function" && superClass !== null) {
    throw new TypeError("Super expression must either be null or a function");
  }
  subClass.prototype = Object.create(superClass && superClass.prototype, {
    constructor: {
      value: subClass,
      writable: true,
      configurable: true
    }
  });
  if (superClass) _setPrototypeOf(subClass, superClass);
}
function _wrapNativeSuper(Class) {
  var _cache = typeof Map === "function" ? new Map() : undefined;
  _wrapNativeSuper = function _wrapNativeSuper(Class) {
    if (Class === null || !_isNativeFunction(Class)) return Class;
    if (typeof Class !== "function") {
      throw new TypeError("Super expression must either be null or a function");
    }
    if (typeof _cache !== "undefined") {
      if (_cache.has(Class)) return _cache.get(Class);
      _cache.set(Class, Wrapper);
    }
    function Wrapper() {
      return _construct(Class, arguments, _getPrototypeOf(this).constructor);
    }
    Wrapper.prototype = Object.create(Class.prototype, {
      constructor: {
        value: Wrapper,
        enumerable: false,
        writable: true,
        configurable: true
      }
    });
    return _setPrototypeOf(Wrapper, Class);
  };
  return _wrapNativeSuper(Class);
}
function _construct(Parent, args, Class) {
  if (_isNativeReflectConstruct()) {
    _construct = Reflect.construct;
  } else {
    _construct = function _construct(Parent, args, Class) {
      var a = [null];
      a.push.apply(a, args);
      var Constructor = Function.bind.apply(Parent, a);
      var instance = new Constructor();
      if (Class) _setPrototypeOf(instance, Class.prototype);
      return instance;
    };
  }
  return _construct.apply(null, arguments);
}
function _isNativeReflectConstruct() {
  if (typeof Reflect === "undefined" || !Reflect.construct) return false;
  if (Reflect.construct.sham) return false;
  if (typeof Proxy === "function") return true;
  try {
    Date.prototype.toString.call(Reflect.construct(Date, [], function () {}));
    return true;
  } catch (e) {
    return false;
  }
}
function _isNativeFunction(fn) {
  return Function.toString.call(fn).indexOf("[native code]") !== -1;
}
function _setPrototypeOf(o, p) {
  _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) {
    o.__proto__ = p;
    return o;
  };
  return _setPrototypeOf(o, p);
}
function _getPrototypeOf(o) {
  _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) {
    return o.__proto__ || Object.getPrototypeOf(o);
  };
  return _getPrototypeOf(o);
}
var ARRAY_WILDCARD = "+";
var PATH_DELIMITER = ".";
var ObjectPrototypeMutationError = /*#__PURE__*/function (_Error) {
  _inherits(ObjectPrototypeMutationError, _Error);
  function ObjectPrototypeMutationError(params) {
    var _this;
    _classCallCheck(this, ObjectPrototypeMutationError);
    _this = _possibleConstructorReturn(this, _getPrototypeOf(ObjectPrototypeMutationError).call(this, params));
    _this.name = "ObjectPrototypeMutationError";
    return _this;
  }
  return ObjectPrototypeMutationError;
}(_wrapNativeSuper(Error));
module.exports = {
  set: setNestedProperty,
  get: getNestedProperty,
  has: hasNestedProperty,
  hasOwn: function hasOwn(object, property, options) {
    return this.has(object, property, options || {
      own: true
    });
  },
  isIn: isInNestedProperty,
  ObjectPrototypeMutationError: ObjectPrototypeMutationError
};
/**
 * Get the property of an object nested in one or more objects or array
 * Given an object such as a.b.c.d = 5, getNestedProperty(a, "b.c.d") will return 5.
 * It also works through arrays. Given a nested array such as a[0].b = 5, getNestedProperty(a, "0.b") will return 5.
 * For accessing nested properties through all items in an array, you may use the array wildcard "+".
 * For instance, getNestedProperty([{a:1}, {a:2}, {a:3}], "+.a") will return [1, 2, 3]
 * @param {Object} object the object to get the property from
 * @param {String} property the path to the property as a string
 * @returns the object or the the property value if found
 */

function getNestedProperty(object, property) {
  if (_typeof(object) != "object" || object === null) {
    return object;
  }
  if (typeof property == "undefined") {
    return object;
  }
  if (typeof property == "number") {
    return object[property];
  }
  try {
    return traverse(object, property, function _getNestedProperty(currentObject, currentProperty) {
      return currentObject[currentProperty];
    });
  } catch (err) {
    return object;
  }
}
/**
 * Tell if a nested object has a given property (or array a given index)
 * given an object such as a.b.c.d = 5, hasNestedProperty(a, "b.c.d") will return true.
 * It also returns true if the property is in the prototype chain.
 * @param {Object} object the object to get the property from
 * @param {String} property the path to the property as a string
 * @param {Object} options:
 *  - own: set to reject properties from the prototype
 * @returns true if has (property in object), false otherwise
 */

function hasNestedProperty(object, property) {
  var options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  if (_typeof(object) != "object" || object === null) {
    return false;
  }
  if (typeof property == "undefined") {
    return false;
  }
  if (typeof property == "number") {
    return property in object;
  }
  try {
    var has = false;
    traverse(object, property, function _hasNestedProperty(currentObject, currentProperty, segments, index) {
      if (isLastSegment(segments, index)) {
        if (options.own) {
          has = currentObject.hasOwnProperty(currentProperty);
        } else {
          has = currentProperty in currentObject;
        }
      } else {
        return currentObject && currentObject[currentProperty];
      }
    });
    return has;
  } catch (err) {
    return false;
  }
}
/**
 * Set the property of an object nested in one or more objects
 * If the property doesn't exist, it gets created.
 * @param {Object} object
 * @param {String} property
 * @param value the value to set
 * @returns object if no assignment was made or the value if the assignment was made
 */

function setNestedProperty(object, property, value) {
  if (_typeof(object) != "object" || object === null) {
    return object;
  }
  if (typeof property == "undefined") {
    return object;
  }
  if (typeof property == "number") {
    object[property] = value;
    return object[property];
  }
  try {
    return traverse(object, property, function _setNestedProperty(currentObject, currentProperty, segments, index) {
      if (currentObject === Reflect.getPrototypeOf({})) {
        throw new ObjectPrototypeMutationError("Attempting to mutate Object.prototype");
      }
      if (!currentObject[currentProperty]) {
        var nextPropIsNumber = Number.isInteger(Number(segments[index + 1]));
        var nextPropIsArrayWildcard = segments[index + 1] === ARRAY_WILDCARD;
        if (nextPropIsNumber || nextPropIsArrayWildcard) {
          currentObject[currentProperty] = [];
        } else {
          currentObject[currentProperty] = {};
        }
      }
      if (isLastSegment(segments, index)) {
        currentObject[currentProperty] = value;
      }
      return currentObject[currentProperty];
    });
  } catch (err) {
    if (err instanceof ObjectPrototypeMutationError) {
      // rethrow
      throw err;
    } else {
      return object;
    }
  }
}
/**
 * Tell if an object is on the path to a nested property
 * If the object is on the path, and the path exists, it returns true, and false otherwise.
 * @param {Object} object to get the nested property from
 * @param {String} property name of the nested property
 * @param {Object} objectInPath the object to check
 * @param {Object} options:
 *  - validPath: return false if the path is invalid, even if the object is in the path
 * @returns {boolean} true if the object is on the path
 */

function isInNestedProperty(object, property, objectInPath) {
  var options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
  if (_typeof(object) != "object" || object === null) {
    return false;
  }
  if (typeof property == "undefined") {
    return false;
  }
  try {
    var isIn = false,
      pathExists = false;
    traverse(object, property, function _isInNestedProperty(currentObject, currentProperty, segments, index) {
      isIn = isIn || currentObject === objectInPath || !!currentObject && currentObject[currentProperty] === objectInPath;
      pathExists = isLastSegment(segments, index) && _typeof(currentObject) === "object" && currentProperty in currentObject;
      return currentObject && currentObject[currentProperty];
    });
    if (options.validPath) {
      return isIn && pathExists;
    } else {
      return isIn;
    }
  } catch (err) {
    return false;
  }
}
function traverse(object, path) {
  var callback = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : function () {};
  var segments = path.split(PATH_DELIMITER);
  var length = segments.length;
  var _loop = function _loop(idx) {
    var currentSegment = segments[idx];
    if (!object) {
      return {
        v: void 0
      };
    }
    if (currentSegment === ARRAY_WILDCARD) {
      if (Array.isArray(object)) {
        return {
          v: object.map(function (value, index) {
            var remainingSegments = segments.slice(idx + 1);
            if (remainingSegments.length > 0) {
              return traverse(value, remainingSegments.join(PATH_DELIMITER), callback);
            } else {
              return callback(object, index, segments, idx);
            }
          })
        };
      } else {
        var pathToHere = segments.slice(0, idx).join(PATH_DELIMITER);
        throw new Error("Object at wildcard (".concat(pathToHere, ") is not an array"));
      }
    } else {
      object = callback(object, currentSegment, segments, idx);
    }
  };
  for (var idx = 0; idx < length; idx++) {
    var _ret = _loop(idx);
    if (_typeof(_ret) === "object") return _ret.v;
  }
  return object;
}
function isLastSegment(segments, index) {
  return segments.length === index + 1;
}

/***/ }),

/***/ 47:
/***/ ((module, __unused_webpack_exports, __webpack_require__) => {

// Copyright Joyent, Inc. and other Node contributors.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.



var util = __webpack_require__(410);
var isString = function (x) {
  return typeof x === 'string';
};

// resolves . and .. elements in a path array with directory names there
// must be no slashes or device names (c:\) in the array
// (so also no leading and trailing slashes - it does not distinguish
// relative and absolute paths)
function normalizeArray(parts, allowAboveRoot) {
  var res = [];
  for (var i = 0; i < parts.length; i++) {
    var p = parts[i];

    // ignore empty parts
    if (!p || p === '.') continue;
    if (p === '..') {
      if (res.length && res[res.length - 1] !== '..') {
        res.pop();
      } else if (allowAboveRoot) {
        res.push('..');
      }
    } else {
      res.push(p);
    }
  }
  return res;
}

// Split a filename into [root, dir, basename, ext], unix version
// 'root' is just a slash, or nothing.
var splitPathRe = /^(\/?|)([\s\S]*?)((?:\.{1,2}|[^\/]+?|)(\.[^.\/]*|))(?:[\/]*)$/;
var posix = {};
function posixSplitPath(filename) {
  return splitPathRe.exec(filename).slice(1);
}

// path.resolve([from ...], to)
// posix version
posix.resolve = function () {
  var resolvedPath = '',
    resolvedAbsolute = false;
  for (var i = arguments.length - 1; i >= -1 && !resolvedAbsolute; i--) {
    var path = i >= 0 ? arguments[i] : process.cwd();

    // Skip empty and invalid entries
    if (!isString(path)) {
      throw new TypeError('Arguments to path.resolve must be strings');
    } else if (!path) {
      continue;
    }
    resolvedPath = path + '/' + resolvedPath;
    resolvedAbsolute = path.charAt(0) === '/';
  }

  // At this point the path should be resolved to a full absolute path, but
  // handle relative paths to be safe (might happen when process.cwd() fails)

  // Normalize the path
  resolvedPath = normalizeArray(resolvedPath.split('/'), !resolvedAbsolute).join('/');
  return (resolvedAbsolute ? '/' : '') + resolvedPath || '.';
};

// path.normalize(path)
// posix version
posix.normalize = function (path) {
  var isAbsolute = posix.isAbsolute(path),
    trailingSlash = path.substr(-1) === '/';

  // Normalize the path
  path = normalizeArray(path.split('/'), !isAbsolute).join('/');
  if (!path && !isAbsolute) {
    path = '.';
  }
  if (path && trailingSlash) {
    path += '/';
  }
  return (isAbsolute ? '/' : '') + path;
};

// posix version
posix.isAbsolute = function (path) {
  return path.charAt(0) === '/';
};

// posix version
posix.join = function () {
  var path = '';
  for (var i = 0; i < arguments.length; i++) {
    var segment = arguments[i];
    if (!isString(segment)) {
      throw new TypeError('Arguments to path.join must be strings');
    }
    if (segment) {
      if (!path) {
        path += segment;
      } else {
        path += '/' + segment;
      }
    }
  }
  return posix.normalize(path);
};

// path.relative(from, to)
// posix version
posix.relative = function (from, to) {
  from = posix.resolve(from).substr(1);
  to = posix.resolve(to).substr(1);
  function trim(arr) {
    var start = 0;
    for (; start < arr.length; start++) {
      if (arr[start] !== '') break;
    }
    var end = arr.length - 1;
    for (; end >= 0; end--) {
      if (arr[end] !== '') break;
    }
    if (start > end) return [];
    return arr.slice(start, end + 1);
  }
  var fromParts = trim(from.split('/'));
  var toParts = trim(to.split('/'));
  var length = Math.min(fromParts.length, toParts.length);
  var samePartsLength = length;
  for (var i = 0; i < length; i++) {
    if (fromParts[i] !== toParts[i]) {
      samePartsLength = i;
      break;
    }
  }
  var outputParts = [];
  for (var i = samePartsLength; i < fromParts.length; i++) {
    outputParts.push('..');
  }
  outputParts = outputParts.concat(toParts.slice(samePartsLength));
  return outputParts.join('/');
};
posix._makeLong = function (path) {
  return path;
};
posix.dirname = function (path) {
  var result = posixSplitPath(path),
    root = result[0],
    dir = result[1];
  if (!root && !dir) {
    // No dirname whatsoever
    return '.';
  }
  if (dir) {
    // It has a dirname, strip trailing slash
    dir = dir.substr(0, dir.length - 1);
  }
  return root + dir;
};
posix.basename = function (path, ext) {
  var f = posixSplitPath(path)[2];
  // TODO: make this comparison case-insensitive on windows?
  if (ext && f.substr(-1 * ext.length) === ext) {
    f = f.substr(0, f.length - ext.length);
  }
  return f;
};
posix.extname = function (path) {
  return posixSplitPath(path)[3];
};
posix.format = function (pathObject) {
  if (!util.isObject(pathObject)) {
    throw new TypeError("Parameter 'pathObject' must be an object, not " + typeof pathObject);
  }
  var root = pathObject.root || '';
  if (!isString(root)) {
    throw new TypeError("'pathObject.root' must be a string or undefined, not " + typeof pathObject.root);
  }
  var dir = pathObject.dir ? pathObject.dir + posix.sep : '';
  var base = pathObject.base || '';
  return dir + base;
};
posix.parse = function (pathString) {
  if (!isString(pathString)) {
    throw new TypeError("Parameter 'pathString' must be a string, not " + typeof pathString);
  }
  var allParts = posixSplitPath(pathString);
  if (!allParts || allParts.length !== 4) {
    throw new TypeError("Invalid path '" + pathString + "'");
  }
  allParts[1] = allParts[1] || '';
  allParts[2] = allParts[2] || '';
  allParts[3] = allParts[3] || '';
  return {
    root: allParts[0],
    dir: allParts[0] + allParts[1].slice(0, allParts[1].length - 1),
    base: allParts[2],
    ext: allParts[3],
    name: allParts[2].slice(0, allParts[2].length - allParts[3].length)
  };
};
posix.sep = '/';
posix.delimiter = ':';
module.exports = posix;

/***/ }),

/***/ 647:
/***/ ((__unused_webpack_module, exports) => {



var has = Object.prototype.hasOwnProperty,
  undef;

/**
 * Decode a URI encoded string.
 *
 * @param {String} input The URI encoded string.
 * @returns {String|Null} The decoded string.
 * @api private
 */
function decode(input) {
  try {
    return decodeURIComponent(input.replace(/\+/g, ' '));
  } catch (e) {
    return null;
  }
}

/**
 * Attempts to encode a given input.
 *
 * @param {String} input The string that needs to be encoded.
 * @returns {String|Null} The encoded string.
 * @api private
 */
function encode(input) {
  try {
    return encodeURIComponent(input);
  } catch (e) {
    return null;
  }
}

/**
 * Simple query string parser.
 *
 * @param {String} query The query string that needs to be parsed.
 * @returns {Object}
 * @api public
 */
function querystring(query) {
  var parser = /([^=?#&]+)=?([^&]*)/g,
    result = {},
    part;
  while (part = parser.exec(query)) {
    var key = decode(part[1]),
      value = decode(part[2]);

    //
    // Prevent overriding of existing properties. This ensures that build-in
    // methods like `toString` or __proto__ are not overriden by malicious
    // querystrings.
    //
    // In the case if failed decoding, we want to omit the key/value pairs
    // from the result.
    //
    if (key === null || value === null || key in result) continue;
    result[key] = value;
  }
  return result;
}

/**
 * Transform a query string to an object.
 *
 * @param {Object} obj Object that should be transformed.
 * @param {String} prefix Optional prefix.
 * @returns {String}
 * @api public
 */
function querystringify(obj, prefix) {
  prefix = prefix || '';
  var pairs = [],
    value,
    key;

  //
  // Optionally prefix with a '?' if needed
  //
  if ('string' !== typeof prefix) prefix = '?';
  for (key in obj) {
    if (has.call(obj, key)) {
      value = obj[key];

      //
      // Edge cases where we actually want to encode the value to an empty
      // string instead of the stringified value.
      //
      if (!value && (value === null || value === undef || isNaN(value))) {
        value = '';
      }
      key = encode(key);
      value = encode(value);

      //
      // If we failed to encode the strings, we should bail out as we don't
      // want to add invalid strings to the query.
      //
      if (key === null || value === null) continue;
      pairs.push(key + '=' + value);
    }
  }
  return pairs.length ? prefix + pairs.join('&') : '';
}

//
// Expose the module.
//
exports.stringify = querystringify;
exports.parse = querystring;

/***/ }),

/***/ 670:
/***/ ((module) => {



/**
 * Check if we're required to add a port number.
 *
 * @see https://url.spec.whatwg.org/#default-port
 * @param {Number|String} port Port number we need to check
 * @param {String} protocol Protocol we need to check against.
 * @returns {Boolean} Is it a default port for the given protocol
 * @api private
 */
module.exports = function required(port, protocol) {
  protocol = protocol.split(':')[0];
  port = +port;
  if (!port) return false;
  switch (protocol) {
    case 'http':
    case 'ws':
      return port !== 80;
    case 'https':
    case 'wss':
      return port !== 443;
    case 'ftp':
      return port !== 21;
    case 'gopher':
      return port !== 70;
    case 'file':
      return false;
  }
  return port !== 0;
};

/***/ }),

/***/ 494:
/***/ ((module) => {

const hexRegex = /^[-+]?0x[a-fA-F0-9]+$/;
const numRegex = /^([\-\+])?(0*)(\.[0-9]+([eE]\-?[0-9]+)?|[0-9]+(\.[0-9]+([eE]\-?[0-9]+)?)?)$/;
// const octRegex = /0x[a-z0-9]+/;
// const binRegex = /0x[a-z0-9]+/;

//polyfill
if (!Number.parseInt && window.parseInt) {
  Number.parseInt = window.parseInt;
}
if (!Number.parseFloat && window.parseFloat) {
  Number.parseFloat = window.parseFloat;
}
const consider = {
  hex: true,
  leadingZeros: true,
  decimalPoint: "\.",
  eNotation: true
  //skipLike: /regex/
};
function toNumber(str) {
  let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  // const options = Object.assign({}, consider);
  // if(opt.leadingZeros === false){
  //     options.leadingZeros = false;
  // }else if(opt.hex === false){
  //     options.hex = false;
  // }

  options = Object.assign({}, consider, options);
  if (!str || typeof str !== "string") return str;
  let trimmedStr = str.trim();
  // if(trimmedStr === "0.0") return 0;
  // else if(trimmedStr === "+0.0") return 0;
  // else if(trimmedStr === "-0.0") return -0;

  if (options.skipLike !== undefined && options.skipLike.test(trimmedStr)) return str;else if (options.hex && hexRegex.test(trimmedStr)) {
    return Number.parseInt(trimmedStr, 16);
    // } else if (options.parseOct && octRegex.test(str)) {
    //     return Number.parseInt(val, 8);
    // }else if (options.parseBin && binRegex.test(str)) {
    //     return Number.parseInt(val, 2);
  } else {
    //separate negative sign, leading zeros, and rest number
    const match = numRegex.exec(trimmedStr);
    if (match) {
      const sign = match[1];
      const leadingZeros = match[2];
      let numTrimmedByZeros = trimZeros(match[3]); //complete num without leading zeros
      //trim ending zeros for floating number

      const eNotation = match[4] || match[6];
      if (!options.leadingZeros && leadingZeros.length > 0 && sign && trimmedStr[2] !== ".") return str; //-0123
      else if (!options.leadingZeros && leadingZeros.length > 0 && !sign && trimmedStr[1] !== ".") return str; //0123
      else {
        //no leading zeros or leading zeros are allowed
        const num = Number(trimmedStr);
        const numStr = "" + num;
        if (numStr.search(/[eE]/) !== -1) {
          //given number is long and parsed to eNotation
          if (options.eNotation) return num;else return str;
        } else if (eNotation) {
          //given number has enotation
          if (options.eNotation) return num;else return str;
        } else if (trimmedStr.indexOf(".") !== -1) {
          //floating number
          // const decimalPart = match[5].substr(1);
          // const intPart = trimmedStr.substr(0,trimmedStr.indexOf("."));

          // const p = numStr.indexOf(".");
          // const givenIntPart = numStr.substr(0,p);
          // const givenDecPart = numStr.substr(p+1);
          if (numStr === "0" && numTrimmedByZeros === "") return num; //0.0
          else if (numStr === numTrimmedByZeros) return num; //0.456. 0.79000
          else if (sign && numStr === "-" + numTrimmedByZeros) return num;else return str;
        }
        if (leadingZeros) {
          // if(numTrimmedByZeros === numStr){
          //     if(options.leadingZeros) return num;
          //     else return str;
          // }else return str;
          if (numTrimmedByZeros === numStr) return num;else if (sign + numTrimmedByZeros === numStr) return num;else return str;
        }
        if (trimmedStr === numStr) return num;else if (trimmedStr === sign + numStr) return num;
        // else{
        //     //number with +/- sign
        //     trimmedStr.test(/[-+][0-9]);

        // }
        return str;
      }
      // else if(!eNotation && trimmedStr && trimmedStr !== Number(trimmedStr) ) return str;
    } else {
      //non-numeric string
      return str;
    }
  }
}

/**
 * 
 * @param {string} numStr without leading zeros
 * @returns 
 */
function trimZeros(numStr) {
  if (numStr && numStr.indexOf(".") !== -1) {
    //float
    numStr = numStr.replace(/0+$/, ""); //remove ending zeros
    if (numStr === ".") numStr = "0";else if (numStr[0] === ".") numStr = "0" + numStr;else if (numStr[numStr.length - 1] === ".") numStr = numStr.substr(0, numStr.length - 1);
    return numStr;
  }
  return numStr;
}
module.exports = toNumber;

/***/ }),

/***/ 737:
/***/ ((module, __unused_webpack_exports, __webpack_require__) => {



var required = __webpack_require__(670),
  qs = __webpack_require__(647),
  controlOrWhitespace = /^[\x00-\x20\u00a0\u1680\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff]+/,
  CRHTLF = /[\n\r\t]/g,
  slashes = /^[A-Za-z][A-Za-z0-9+-.]*:\/\//,
  port = /:\d+$/,
  protocolre = /^([a-z][a-z0-9.+-]*:)?(\/\/)?([\\/]+)?([\S\s]*)/i,
  windowsDriveLetter = /^[a-zA-Z]:/;

/**
 * Remove control characters and whitespace from the beginning of a string.
 *
 * @param {Object|String} str String to trim.
 * @returns {String} A new string representing `str` stripped of control
 *     characters and whitespace from its beginning.
 * @public
 */
function trimLeft(str) {
  return (str ? str : '').toString().replace(controlOrWhitespace, '');
}

/**
 * These are the parse rules for the URL parser, it informs the parser
 * about:
 *
 * 0. The char it Needs to parse, if it's a string it should be done using
 *    indexOf, RegExp using exec and NaN means set as current value.
 * 1. The property we should set when parsing this value.
 * 2. Indication if it's backwards or forward parsing, when set as number it's
 *    the value of extra chars that should be split off.
 * 3. Inherit from location if non existing in the parser.
 * 4. `toLowerCase` the resulting value.
 */
var rules = [['#', 'hash'],
// Extract from the back.
['?', 'query'],
// Extract from the back.
function sanitize(address, url) {
  // Sanitize what is left of the address
  return isSpecial(url.protocol) ? address.replace(/\\/g, '/') : address;
}, ['/', 'pathname'],
// Extract from the back.
['@', 'auth', 1],
// Extract from the front.
[NaN, 'host', undefined, 1, 1],
// Set left over value.
[/:(\d*)$/, 'port', undefined, 1],
// RegExp the back.
[NaN, 'hostname', undefined, 1, 1] // Set left over.
];

/**
 * These properties should not be copied or inherited from. This is only needed
 * for all non blob URL's as a blob URL does not include a hash, only the
 * origin.
 *
 * @type {Object}
 * @private
 */
var ignore = {
  hash: 1,
  query: 1
};

/**
 * The location object differs when your code is loaded through a normal page,
 * Worker or through a worker using a blob. And with the blobble begins the
 * trouble as the location object will contain the URL of the blob, not the
 * location of the page where our code is loaded in. The actual origin is
 * encoded in the `pathname` so we can thankfully generate a good "default"
 * location from it so we can generate proper relative URL's again.
 *
 * @param {Object|String} loc Optional default location object.
 * @returns {Object} lolcation object.
 * @public
 */
function lolcation(loc) {
  var globalVar;
  if (typeof window !== 'undefined') globalVar = window;else if (typeof global !== 'undefined') globalVar = global;else if (typeof self !== 'undefined') globalVar = self;else globalVar = {};
  var location = globalVar.location || {};
  loc = loc || location;
  var finaldestination = {},
    type = typeof loc,
    key;
  if ('blob:' === loc.protocol) {
    finaldestination = new Url(unescape(loc.pathname), {});
  } else if ('string' === type) {
    finaldestination = new Url(loc, {});
    for (key in ignore) delete finaldestination[key];
  } else if ('object' === type) {
    for (key in loc) {
      if (key in ignore) continue;
      finaldestination[key] = loc[key];
    }
    if (finaldestination.slashes === undefined) {
      finaldestination.slashes = slashes.test(loc.href);
    }
  }
  return finaldestination;
}

/**
 * Check whether a protocol scheme is special.
 *
 * @param {String} The protocol scheme of the URL
 * @return {Boolean} `true` if the protocol scheme is special, else `false`
 * @private
 */
function isSpecial(scheme) {
  return scheme === 'file:' || scheme === 'ftp:' || scheme === 'http:' || scheme === 'https:' || scheme === 'ws:' || scheme === 'wss:';
}

/**
 * @typedef ProtocolExtract
 * @type Object
 * @property {String} protocol Protocol matched in the URL, in lowercase.
 * @property {Boolean} slashes `true` if protocol is followed by "//", else `false`.
 * @property {String} rest Rest of the URL that is not part of the protocol.
 */

/**
 * Extract protocol information from a URL with/without double slash ("//").
 *
 * @param {String} address URL we want to extract from.
 * @param {Object} location
 * @return {ProtocolExtract} Extracted information.
 * @private
 */
function extractProtocol(address, location) {
  address = trimLeft(address);
  address = address.replace(CRHTLF, '');
  location = location || {};
  var match = protocolre.exec(address);
  var protocol = match[1] ? match[1].toLowerCase() : '';
  var forwardSlashes = !!match[2];
  var otherSlashes = !!match[3];
  var slashesCount = 0;
  var rest;
  if (forwardSlashes) {
    if (otherSlashes) {
      rest = match[2] + match[3] + match[4];
      slashesCount = match[2].length + match[3].length;
    } else {
      rest = match[2] + match[4];
      slashesCount = match[2].length;
    }
  } else {
    if (otherSlashes) {
      rest = match[3] + match[4];
      slashesCount = match[3].length;
    } else {
      rest = match[4];
    }
  }
  if (protocol === 'file:') {
    if (slashesCount >= 2) {
      rest = rest.slice(2);
    }
  } else if (isSpecial(protocol)) {
    rest = match[4];
  } else if (protocol) {
    if (forwardSlashes) {
      rest = rest.slice(2);
    }
  } else if (slashesCount >= 2 && isSpecial(location.protocol)) {
    rest = match[4];
  }
  return {
    protocol: protocol,
    slashes: forwardSlashes || isSpecial(protocol),
    slashesCount: slashesCount,
    rest: rest
  };
}

/**
 * Resolve a relative URL pathname against a base URL pathname.
 *
 * @param {String} relative Pathname of the relative URL.
 * @param {String} base Pathname of the base URL.
 * @return {String} Resolved pathname.
 * @private
 */
function resolve(relative, base) {
  if (relative === '') return base;
  var path = (base || '/').split('/').slice(0, -1).concat(relative.split('/')),
    i = path.length,
    last = path[i - 1],
    unshift = false,
    up = 0;
  while (i--) {
    if (path[i] === '.') {
      path.splice(i, 1);
    } else if (path[i] === '..') {
      path.splice(i, 1);
      up++;
    } else if (up) {
      if (i === 0) unshift = true;
      path.splice(i, 1);
      up--;
    }
  }
  if (unshift) path.unshift('');
  if (last === '.' || last === '..') path.push('');
  return path.join('/');
}

/**
 * The actual URL instance. Instead of returning an object we've opted-in to
 * create an actual constructor as it's much more memory efficient and
 * faster and it pleases my OCD.
 *
 * It is worth noting that we should not use `URL` as class name to prevent
 * clashes with the global URL instance that got introduced in browsers.
 *
 * @constructor
 * @param {String} address URL we want to parse.
 * @param {Object|String} [location] Location defaults for relative paths.
 * @param {Boolean|Function} [parser] Parser for the query string.
 * @private
 */
function Url(address, location, parser) {
  address = trimLeft(address);
  address = address.replace(CRHTLF, '');
  if (!(this instanceof Url)) {
    return new Url(address, location, parser);
  }
  var relative,
    extracted,
    parse,
    instruction,
    index,
    key,
    instructions = rules.slice(),
    type = typeof location,
    url = this,
    i = 0;

  //
  // The following if statements allows this module two have compatibility with
  // 2 different API:
  //
  // 1. Node.js's `url.parse` api which accepts a URL, boolean as arguments
  //    where the boolean indicates that the query string should also be parsed.
  //
  // 2. The `URL` interface of the browser which accepts a URL, object as
  //    arguments. The supplied object will be used as default values / fall-back
  //    for relative paths.
  //
  if ('object' !== type && 'string' !== type) {
    parser = location;
    location = null;
  }
  if (parser && 'function' !== typeof parser) parser = qs.parse;
  location = lolcation(location);

  //
  // Extract protocol information before running the instructions.
  //
  extracted = extractProtocol(address || '', location);
  relative = !extracted.protocol && !extracted.slashes;
  url.slashes = extracted.slashes || relative && location.slashes;
  url.protocol = extracted.protocol || location.protocol || '';
  address = extracted.rest;

  //
  // When the authority component is absent the URL starts with a path
  // component.
  //
  if (extracted.protocol === 'file:' && (extracted.slashesCount !== 2 || windowsDriveLetter.test(address)) || !extracted.slashes && (extracted.protocol || extracted.slashesCount < 2 || !isSpecial(url.protocol))) {
    instructions[3] = [/(.*)/, 'pathname'];
  }
  for (; i < instructions.length; i++) {
    instruction = instructions[i];
    if (typeof instruction === 'function') {
      address = instruction(address, url);
      continue;
    }
    parse = instruction[0];
    key = instruction[1];
    if (parse !== parse) {
      url[key] = address;
    } else if ('string' === typeof parse) {
      index = parse === '@' ? address.lastIndexOf(parse) : address.indexOf(parse);
      if (~index) {
        if ('number' === typeof instruction[2]) {
          url[key] = address.slice(0, index);
          address = address.slice(index + instruction[2]);
        } else {
          url[key] = address.slice(index);
          address = address.slice(0, index);
        }
      }
    } else if (index = parse.exec(address)) {
      url[key] = index[1];
      address = address.slice(0, index.index);
    }
    url[key] = url[key] || (relative && instruction[3] ? location[key] || '' : '');

    //
    // Hostname, host and protocol should be lowercased so they can be used to
    // create a proper `origin`.
    //
    if (instruction[4]) url[key] = url[key].toLowerCase();
  }

  //
  // Also parse the supplied query string in to an object. If we're supplied
  // with a custom parser as function use that instead of the default build-in
  // parser.
  //
  if (parser) url.query = parser(url.query);

  //
  // If the URL is relative, resolve the pathname against the base URL.
  //
  if (relative && location.slashes && url.pathname.charAt(0) !== '/' && (url.pathname !== '' || location.pathname !== '')) {
    url.pathname = resolve(url.pathname, location.pathname);
  }

  //
  // Default to a / for pathname if none exists. This normalizes the URL
  // to always have a /
  //
  if (url.pathname.charAt(0) !== '/' && isSpecial(url.protocol)) {
    url.pathname = '/' + url.pathname;
  }

  //
  // We should not add port numbers if they are already the default port number
  // for a given protocol. As the host also contains the port number we're going
  // override it with the hostname which contains no port number.
  //
  if (!required(url.port, url.protocol)) {
    url.host = url.hostname;
    url.port = '';
  }

  //
  // Parse down the `auth` for the username and password.
  //
  url.username = url.password = '';
  if (url.auth) {
    index = url.auth.indexOf(':');
    if (~index) {
      url.username = url.auth.slice(0, index);
      url.username = encodeURIComponent(decodeURIComponent(url.username));
      url.password = url.auth.slice(index + 1);
      url.password = encodeURIComponent(decodeURIComponent(url.password));
    } else {
      url.username = encodeURIComponent(decodeURIComponent(url.auth));
    }
    url.auth = url.password ? url.username + ':' + url.password : url.username;
  }
  url.origin = url.protocol !== 'file:' && isSpecial(url.protocol) && url.host ? url.protocol + '//' + url.host : 'null';

  //
  // The href is just the compiled result.
  //
  url.href = url.toString();
}

/**
 * This is convenience method for changing properties in the URL instance to
 * insure that they all propagate correctly.
 *
 * @param {String} part          Property we need to adjust.
 * @param {Mixed} value          The newly assigned value.
 * @param {Boolean|Function} fn  When setting the query, it will be the function
 *                               used to parse the query.
 *                               When setting the protocol, double slash will be
 *                               removed from the final url if it is true.
 * @returns {URL} URL instance for chaining.
 * @public
 */
function set(part, value, fn) {
  var url = this;
  switch (part) {
    case 'query':
      if ('string' === typeof value && value.length) {
        value = (fn || qs.parse)(value);
      }
      url[part] = value;
      break;
    case 'port':
      url[part] = value;
      if (!required(value, url.protocol)) {
        url.host = url.hostname;
        url[part] = '';
      } else if (value) {
        url.host = url.hostname + ':' + value;
      }
      break;
    case 'hostname':
      url[part] = value;
      if (url.port) value += ':' + url.port;
      url.host = value;
      break;
    case 'host':
      url[part] = value;
      if (port.test(value)) {
        value = value.split(':');
        url.port = value.pop();
        url.hostname = value.join(':');
      } else {
        url.hostname = value;
        url.port = '';
      }
      break;
    case 'protocol':
      url.protocol = value.toLowerCase();
      url.slashes = !fn;
      break;
    case 'pathname':
    case 'hash':
      if (value) {
        var char = part === 'pathname' ? '/' : '#';
        url[part] = value.charAt(0) !== char ? char + value : value;
      } else {
        url[part] = value;
      }
      break;
    case 'username':
    case 'password':
      url[part] = encodeURIComponent(value);
      break;
    case 'auth':
      var index = value.indexOf(':');
      if (~index) {
        url.username = value.slice(0, index);
        url.username = encodeURIComponent(decodeURIComponent(url.username));
        url.password = value.slice(index + 1);
        url.password = encodeURIComponent(decodeURIComponent(url.password));
      } else {
        url.username = encodeURIComponent(decodeURIComponent(value));
      }
  }
  for (var i = 0; i < rules.length; i++) {
    var ins = rules[i];
    if (ins[4]) url[ins[1]] = url[ins[1]].toLowerCase();
  }
  url.auth = url.password ? url.username + ':' + url.password : url.username;
  url.origin = url.protocol !== 'file:' && isSpecial(url.protocol) && url.host ? url.protocol + '//' + url.host : 'null';
  url.href = url.toString();
  return url;
}

/**
 * Transform the properties back in to a valid and full URL string.
 *
 * @param {Function} stringify Optional query stringify function.
 * @returns {String} Compiled version of the URL.
 * @public
 */
function toString(stringify) {
  if (!stringify || 'function' !== typeof stringify) stringify = qs.stringify;
  var query,
    url = this,
    host = url.host,
    protocol = url.protocol;
  if (protocol && protocol.charAt(protocol.length - 1) !== ':') protocol += ':';
  var result = protocol + (url.protocol && url.slashes || isSpecial(url.protocol) ? '//' : '');
  if (url.username) {
    result += url.username;
    if (url.password) result += ':' + url.password;
    result += '@';
  } else if (url.password) {
    result += ':' + url.password;
    result += '@';
  } else if (url.protocol !== 'file:' && isSpecial(url.protocol) && !host && url.pathname !== '/') {
    //
    // Add back the empty userinfo, otherwise the original invalid URL
    // might be transformed into a valid one with `url.pathname` as host.
    //
    result += '@';
  }

  //
  // Trailing colon is removed from `url.host` when it is parsed. If it still
  // ends with a colon, then add back the trailing colon that was removed. This
  // prevents an invalid URL from being transformed into a valid one.
  //
  if (host[host.length - 1] === ':' || port.test(url.hostname) && !url.port) {
    host += ':';
  }
  result += host + url.pathname;
  query = 'object' === typeof url.query ? stringify(url.query) : url.query;
  if (query) result += '?' !== query.charAt(0) ? '?' + query : query;
  if (url.hash) result += url.hash;
  return result;
}
Url.prototype = {
  set: set,
  toString: toString
};

//
// Expose the URL parser and some additional properties that might be useful for
// others or testing.
//
Url.extractProtocol = extractProtocol;
Url.location = lolcation;
Url.trimLeft = trimLeft;
Url.qs = qs;
module.exports = Url;

/***/ }),

/***/ 410:
/***/ (() => {

/* (ignored) */

/***/ }),

/***/ 388:
/***/ (() => {

/* (ignored) */

/***/ }),

/***/ 805:
/***/ (() => {

/* (ignored) */

/***/ }),

/***/ 345:
/***/ (() => {

/* (ignored) */

/***/ }),

/***/ 800:
/***/ (() => {

/* (ignored) */

/***/ })

/******/ });
/************************************************************************/
/******/ // The module cache
/******/ var __webpack_module_cache__ = {};
/******/ 
/******/ // The require function
/******/ function __webpack_require__(moduleId) {
/******/ 	// Check if module is in cache
/******/ 	var cachedModule = __webpack_module_cache__[moduleId];
/******/ 	if (cachedModule !== undefined) {
/******/ 		return cachedModule.exports;
/******/ 	}
/******/ 	// Create a new module (and put it into the cache)
/******/ 	var module = __webpack_module_cache__[moduleId] = {
/******/ 		id: moduleId,
/******/ 		loaded: false,
/******/ 		exports: {}
/******/ 	};
/******/ 
/******/ 	// Execute the module function
/******/ 	__webpack_modules__[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/ 
/******/ 	// Flag the module as loaded
/******/ 	module.loaded = true;
/******/ 
/******/ 	// Return the exports of the module
/******/ 	return module.exports;
/******/ }
/******/ 
/************************************************************************/
/******/ /* webpack/runtime/compat get default export */
/******/ (() => {
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = (module) => {
/******/ 		var getter = module && module.__esModule ?
/******/ 			() => (module['default']) :
/******/ 			() => (module);
/******/ 		__webpack_require__.d(getter, { a: getter });
/******/ 		return getter;
/******/ 	};
/******/ })();
/******/ 
/******/ /* webpack/runtime/define property getters */
/******/ (() => {
/******/ 	// define getter functions for harmony exports
/******/ 	__webpack_require__.d = (exports, definition) => {
/******/ 		for(var key in definition) {
/******/ 			if(__webpack_require__.o(definition, key) && !__webpack_require__.o(exports, key)) {
/******/ 				Object.defineProperty(exports, key, { enumerable: true, get: definition[key] });
/******/ 			}
/******/ 		}
/******/ 	};
/******/ })();
/******/ 
/******/ /* webpack/runtime/hasOwnProperty shorthand */
/******/ (() => {
/******/ 	__webpack_require__.o = (obj, prop) => (Object.prototype.hasOwnProperty.call(obj, prop))
/******/ })();
/******/ 
/******/ /* webpack/runtime/node module decorator */
/******/ (() => {
/******/ 	__webpack_require__.nmd = (module) => {
/******/ 		module.paths = [];
/******/ 		if (!module.children) module.children = [];
/******/ 		return module;
/******/ 	};
/******/ })();
/******/ 
/************************************************************************/
var __webpack_exports__ = {};

// EXPORTS
__webpack_require__.d(__webpack_exports__, {
  hT: () => (/* reexport */ AuthType),
  O4: () => (/* reexport */ ErrorCode),
  Kd: () => (/* reexport */ Request),
  YK: () => (/* reexport */ Response),
  UU: () => (/* reexport */ createClient),
  Gu: () => (/* reexport */ getPatcher),
  ky: () => (/* reexport */ parseStat),
  h4: () => (/* reexport */ parseXML),
  ch: () => (/* reexport */ prepareFileFromProps),
  hq: () => (/* reexport */ processResponsePayload),
  i5: () => (/* reexport */ translateDiskSpace)
});

// EXTERNAL MODULE: ./node_modules/url-parse/index.js
var url_parse = __webpack_require__(737);
var url_parse_default = /*#__PURE__*/__webpack_require__.n(url_parse);
;// CONCATENATED MODULE: ./node_modules/layerr/dist/error.js
function assertError(err) {
  if (!isError(err)) {
    throw new Error("Parameter was not an error");
  }
}
function isError(err) {
  return !!err && typeof err === "object" && objectToString(err) === "[object Error]" || err instanceof Error;
}
function objectToString(obj) {
  return Object.prototype.toString.call(obj);
}
;// CONCATENATED MODULE: ./node_modules/layerr/dist/global.js
const NAME = "Layerr";
let __name = NAME;
function getGlobalName() {
  return __name;
}
function setGlobalName() {
  let name = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : null;
  __name = name ?? NAME;
}
;// CONCATENATED MODULE: ./node_modules/layerr/dist/tools.js

function parseArguments(args) {
  let options,
    shortMessage = "";
  if (args.length === 0) {
    options = {};
  } else if (isError(args[0])) {
    options = {
      cause: args[0]
    };
    shortMessage = args.slice(1).join(" ") || "";
  } else if (args[0] && typeof args[0] === "object") {
    options = Object.assign({}, args[0]);
    shortMessage = args.slice(1).join(" ") || "";
  } else if (typeof args[0] === "string") {
    options = {};
    shortMessage = shortMessage = args.join(" ") || "";
  } else {
    throw new Error("Invalid arguments passed to Layerr");
  }
  return {
    options,
    shortMessage
  };
}
;// CONCATENATED MODULE: ./node_modules/layerr/dist/layerr.js



class Layerr extends Error {
  constructor(errorOptionsOrMessage, messageText) {
    const args = [...arguments];
    const {
      options,
      shortMessage
    } = parseArguments(args);
    let message = shortMessage;
    if (options.cause) {
      message = `${message}: ${options.cause.message}`;
    }
    super(message);
    this.message = message;
    if (options.name && typeof options.name === "string") {
      this.name = options.name;
    } else {
      this.name = getGlobalName();
    }
    if (options.cause) {
      Object.defineProperty(this, "_cause", {
        value: options.cause
      });
    }
    Object.defineProperty(this, "_info", {
      value: {}
    });
    if (options.info && typeof options.info === "object") {
      Object.assign(this._info, options.info);
    }
    if (Error.captureStackTrace) {
      const ctor = options.constructorOpt || this.constructor;
      Error.captureStackTrace(this, ctor);
    }
  }
  static cause(err) {
    assertError(err);
    if (!err._cause) return null;
    return isError(err._cause) ? err._cause : null;
  }
  static fullStack(err) {
    assertError(err);
    const cause = Layerr.cause(err);
    if (cause) {
      return `${err.stack}\ncaused by: ${Layerr.fullStack(cause)}`;
    }
    return err.stack ?? "";
  }
  static info(err) {
    assertError(err);
    const output = {};
    const cause = Layerr.cause(err);
    if (cause) {
      Object.assign(output, Layerr.info(cause));
    }
    if (err._info) {
      Object.assign(output, err._info);
    }
    return output;
  }
  toString() {
    let output = this.name || this.constructor.name || this.constructor.prototype.name;
    if (this.message) {
      output = `${output}: ${this.message}`;
    }
    return output;
  }
}
;// CONCATENATED MODULE: ./node_modules/layerr/dist/index.js




;// CONCATENATED MODULE: ./node_modules/url-join/lib/url-join.js
function normalize(strArray) {
  var resultArray = [];
  if (strArray.length === 0) {
    return '';
  }
  if (typeof strArray[0] !== 'string') {
    throw new TypeError('Url must be a string. Received ' + strArray[0]);
  }

  // If the first part is a plain protocol, we combine it with the next part.
  if (strArray[0].match(/^[^/:]+:\/*$/) && strArray.length > 1) {
    var first = strArray.shift();
    strArray[0] = first + strArray[0];
  }

  // There must be two or three slashes in the file protocol, two slashes in anything else.
  if (strArray[0].match(/^file:\/\/\//)) {
    strArray[0] = strArray[0].replace(/^([^/:]+):\/*/, '$1:///');
  } else {
    strArray[0] = strArray[0].replace(/^([^/:]+):\/*/, '$1://');
  }
  for (var i = 0; i < strArray.length; i++) {
    var component = strArray[i];
    if (typeof component !== 'string') {
      throw new TypeError('Url must be a string. Received ' + component);
    }
    if (component === '') {
      continue;
    }
    if (i > 0) {
      // Removing the starting slashes for each component but the first.
      component = component.replace(/^[\/]+/, '');
    }
    if (i < strArray.length - 1) {
      // Removing the ending slashes for each component but the last.
      component = component.replace(/[\/]+$/, '');
    } else {
      // For the last component we will combine multiple slashes to a single one.
      component = component.replace(/[\/]+$/, '/');
    }
    resultArray.push(component);
  }
  var str = resultArray.join('/');
  // Each input component is now separated by a single slash except the possible first plain protocol part.

  // remove trailing slash before parameters or hash
  str = str.replace(/\/(\?|&|#[^!])/g, '$1');

  // replace ? in parameters with &
  var parts = str.split('?');
  str = parts.shift() + (parts.length > 0 ? '?' : '') + parts.join('&');
  return str;
}
function urlJoin() {
  var input;
  if (typeof arguments[0] === 'object') {
    input = arguments[0];
  } else {
    input = [].slice.call(arguments);
  }
  return normalize(input);
}
// EXTERNAL MODULE: ./node_modules/path-posix/index.js
var path_posix = __webpack_require__(47);
var path_posix_default = /*#__PURE__*/__webpack_require__.n(path_posix);
;// CONCATENATED MODULE: ./source/tools/path.ts


const SEP_PATH_POSIX = "__PATH_SEPARATOR_POSIX__";
const SEP_PATH_WINDOWS = "__PATH_SEPARATOR_WINDOWS__";
function encodePath(filePath) {
  try {
    const replaced = filePath.replace(/\//g, SEP_PATH_POSIX).replace(/\\\\/g, SEP_PATH_WINDOWS);
    const formatted = encodeURIComponent(replaced);
    return formatted.split(SEP_PATH_WINDOWS).join("\\\\").split(SEP_PATH_POSIX).join("/");
  } catch (err) {
    throw new Layerr(err, "Failed encoding path");
  }
}
function getAllDirectories(directory) {
  if (!directory || directory === "/") return [];
  let currentPath = directory;
  const output = [];
  do {
    output.push(currentPath);
    currentPath = path_posix_default().dirname(currentPath);
  } while (currentPath && currentPath !== "/");
  return output;
}
function makePathAbsolute(pathStr) {
  return pathStr.startsWith("/") ? pathStr : "/" + pathStr;
}
function normalisePath(pathStr) {
  let normalisedPath = pathStr;
  if (normalisedPath[0] !== "/") {
    normalisedPath = "/" + normalisedPath;
  }
  if (/^.+\/$/.test(normalisedPath)) {
    normalisedPath = normalisedPath.substr(0, normalisedPath.length - 1);
  }
  return normalisedPath;
}
;// CONCATENATED MODULE: ./source/tools/url.ts




function extractURLPath(fullURL) {
  const url = new (url_parse_default())(fullURL);
  let urlPath = url.pathname;
  if (urlPath.length <= 0) {
    urlPath = "/";
  }
  return normalisePath(urlPath);
}
function joinURL() {
  for (var _len = arguments.length, parts = new Array(_len), _key = 0; _key < _len; _key++) {
    parts[_key] = arguments[_key];
  }
  return urlJoin(parts.reduce((output, nextPart, partIndex) => {
    if (partIndex === 0 || nextPart !== "/" || nextPart === "/" && output[output.length - 1] !== "/") {
      output.push(nextPart);
    }
    return output;
  }, []));
}
function normaliseHREF(href) {
  try {
    const normalisedHref = href.replace(/^https?:\/\/[^\/]+/, "");
    return normalisedHref;
  } catch (err) {
    throw new Layerr(err, "Failed normalising HREF");
  }
}
// EXTERNAL MODULE: ./node_modules/md5/md5.js
var md5 = __webpack_require__(542);
var md5_default = /*#__PURE__*/__webpack_require__.n(md5);
;// CONCATENATED MODULE: ./source/tools/crypto.ts

function ha1Compute(algorithm, user, realm, pass, nonce, cnonce, ha1) {
  const ha1Hash = ha1 || md5_default()(`${user}:${realm}:${pass}`);
  if (algorithm && algorithm.toLowerCase() === "md5-sess") {
    return md5_default()(`${ha1Hash}:${nonce}:${cnonce}`);
  }
  return ha1Hash;
}
;// CONCATENATED MODULE: ./source/auth/digest.ts


const NONCE_CHARS = "abcdef0123456789";
const NONCE_SIZE = 32;
function createDigestContext(username, password, ha1) {
  return {
    username,
    password,
    ha1,
    nc: 0,
    algorithm: "md5",
    hasDigestAuth: false
  };
}
function generateDigestAuthHeader(options, digest) {
  const url = options.url.replace("//", "");
  const uri = url.indexOf("/") == -1 ? "/" : url.slice(url.indexOf("/"));
  const method = options.method ? options.method.toUpperCase() : "GET";
  const qop = /(^|,)\s*auth\s*($|,)/.test(digest.qop) ? "auth" : false;
  const ncString = `00000000${digest.nc}`.slice(-8);
  const ha1 = ha1Compute(digest.algorithm, digest.username, digest.realm, digest.password, digest.nonce, digest.cnonce, digest.ha1);
  const ha2 = md5_default()(`${method}:${uri}`);
  const digestResponse = qop ? md5_default()(`${ha1}:${digest.nonce}:${ncString}:${digest.cnonce}:${qop}:${ha2}`) : md5_default()(`${ha1}:${digest.nonce}:${ha2}`);
  const authValues = {
    username: digest.username,
    realm: digest.realm,
    nonce: digest.nonce,
    uri,
    qop,
    response: digestResponse,
    nc: ncString,
    cnonce: digest.cnonce,
    algorithm: digest.algorithm,
    opaque: digest.opaque
  };
  const authHeader = [];
  for (const k in authValues) {
    if (authValues[k]) {
      if (k === "qop" || k === "nc" || k === "algorithm") {
        authHeader.push(`${k}=${authValues[k]}`);
      } else {
        authHeader.push(`${k}="${authValues[k]}"`);
      }
    }
  }
  return `Digest ${authHeader.join(", ")}`;
}
function makeNonce() {
  let uid = "";
  for (let i = 0; i < NONCE_SIZE; ++i) {
    uid = `${uid}${NONCE_CHARS[Math.floor(Math.random() * NONCE_CHARS.length)]}`;
  }
  return uid;
}
function parseDigestAuth(response, _digest) {
  const isDigest = responseIndicatesDigestAuth(response);
  if (!isDigest) {
    return false;
  }
  const re = /([a-z0-9_-]+)=(?:"([^"]+)"|([a-z0-9_-]+))/gi;
  for (;;) {
    const authHeader = response.headers && response.headers.get("www-authenticate") || "";
    const match = re.exec(authHeader);
    if (!match) {
      break;
    }
    _digest[match[1]] = match[2] || match[3];
  }
  _digest.nc += 1;
  _digest.cnonce = makeNonce();
  return true;
}
function responseIndicatesDigestAuth(response) {
  const authHeader = response.headers && response.headers.get("www-authenticate") || "";
  return authHeader.split(/\s/)[0].toLowerCase() === "digest";
}
// EXTERNAL MODULE: ./node_modules/base-64/base64.js
var base64 = __webpack_require__(101);
var base64_default = /*#__PURE__*/__webpack_require__.n(base64);
;// CONCATENATED MODULE: ./source/tools/encode.ts



function decodeHTMLEntities(text) {
  if (isWeb()) {
    const txt = document.createElement("textarea");
    txt.innerHTML = text;
    return txt.value;
  }
  return decodeHTML(text);
}
function fromBase64(text) {
  return base64_default().decode(text);
}
function toBase64(text) {
  return base64_default().encode(text);
}
;// CONCATENATED MODULE: ./source/auth/basic.ts

function generateBasicAuthHeader(username, password) {
  const encoded = toBase64(`${username}:${password}`);
  return `Basic ${encoded}`;
}
;// CONCATENATED MODULE: ./source/auth/oauth.ts
function generateTokenAuthHeader(token) {
  return `${token.token_type} ${token.access_token}`;
}
;// CONCATENATED MODULE: ./node_modules/@buttercup/fetch/dist/index.browser.js
const inWebWorker = typeof WorkerGlobalScope !== "undefined" && self instanceof WorkerGlobalScope;
const root = inWebWorker ? self : typeof window !== "undefined" ? window : globalThis;
const fetch = root.fetch.bind(root);
const Headers = root.Headers;
const Request = root.Request;
const Response = root.Response;
;// CONCATENATED MODULE: ./source/types.ts

let AuthType = /*#__PURE__*/function (AuthType) {
  AuthType["Auto"] = "auto";
  AuthType["Digest"] = "digest";
  AuthType["None"] = "none";
  AuthType["Password"] = "password";
  AuthType["Token"] = "token";
  return AuthType;
}({});

/** <propstat> as per http://www.webdav.org/specs/rfc2518.html#rfc.section.12.9.1.1 */

/**
 * DAV response can either be (href, propstat, responsedescription?) or (href, status, responsedescription?)
 * @see http://www.webdav.org/specs/rfc2518.html#rfc.section.12.9.1
 */

let ErrorCode = /*#__PURE__*/function (ErrorCode) {
  ErrorCode["DataTypeNoLength"] = "data-type-no-length";
  ErrorCode["InvalidAuthType"] = "invalid-auth-type";
  ErrorCode["InvalidOutputFormat"] = "invalid-output-format";
  ErrorCode["LinkUnsupportedAuthType"] = "link-unsupported-auth";
  ErrorCode["InvalidUpdateRange"] = "invalid-update-range";
  ErrorCode["NotSupported"] = "not-supported";
  return ErrorCode;
}({});
;// CONCATENATED MODULE: ./source/auth/index.ts





function setupAuth(context, username, password, oauthToken, ha1) {
  switch (context.authType) {
    case AuthType.Auto:
      if (username && password) {
        context.headers.Authorization = generateBasicAuthHeader(username, password);
      }
      break;
    case AuthType.Digest:
      context.digest = createDigestContext(username, password, ha1);
      break;
    case AuthType.None:
      // Do nothing
      break;
    case AuthType.Password:
      context.headers.Authorization = generateBasicAuthHeader(username, password);
      break;
    case AuthType.Token:
      context.headers.Authorization = generateTokenAuthHeader(oauthToken);
      break;
    default:
      throw new Layerr({
        info: {
          code: ErrorCode.InvalidAuthType
        }
      }, `Invalid auth type: ${context.authType}`);
  }
}
// EXTERNAL MODULE: http (ignored)
var http_ignored_ = __webpack_require__(345);
// EXTERNAL MODULE: https (ignored)
var https_ignored_ = __webpack_require__(800);
;// CONCATENATED MODULE: ./node_modules/hot-patcher/dist/functions.js
function sequence() {
  for (var _len = arguments.length, methods = new Array(_len), _key = 0; _key < _len; _key++) {
    methods[_key] = arguments[_key];
  }
  if (methods.length === 0) {
    throw new Error("Failed creating sequence: No functions provided");
  }
  return function __executeSequence() {
    for (var _len2 = arguments.length, args = new Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
      args[_key2] = arguments[_key2];
    }
    let result = args;
    const _this = this;
    while (methods.length > 0) {
      const method = methods.shift();
      result = [method.apply(_this, result)];
    }
    return result[0];
  };
}
;// CONCATENATED MODULE: ./node_modules/hot-patcher/dist/patcher.js

const HOT_PATCHER_TYPE = "@@HOTPATCHER";
const NOOP = () => {};
function createNewItem(method) {
  return {
    original: method,
    methods: [method],
    final: false
  };
}
/**
 * Hot patching manager class
 */
class HotPatcher {
  constructor() {
    this._configuration = {
      registry: {},
      getEmptyAction: "null"
    };
    this.__type__ = HOT_PATCHER_TYPE;
  }
  /**
   * Configuration object reference
   * @readonly
   */
  get configuration() {
    return this._configuration;
  }
  /**
   * The action to take when a non-set method is requested
   * Possible values: null/throw
   */
  get getEmptyAction() {
    return this.configuration.getEmptyAction;
  }
  set getEmptyAction(newAction) {
    this.configuration.getEmptyAction = newAction;
  }
  /**
   * Control another hot-patcher instance
   * Force the remote instance to use patched methods from calling instance
   * @param target The target instance to control
   * @param allowTargetOverrides Allow the target to override patched methods on
   * the controller (default is false)
   * @returns Returns self
   * @throws {Error} Throws if the target is invalid
   */
  control(target) {
    let allowTargetOverrides = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
    if (!target || target.__type__ !== HOT_PATCHER_TYPE) {
      throw new Error("Failed taking control of target HotPatcher instance: Invalid type or object");
    }
    Object.keys(target.configuration.registry).forEach(foreignKey => {
      if (this.configuration.registry.hasOwnProperty(foreignKey)) {
        if (allowTargetOverrides) {
          this.configuration.registry[foreignKey] = Object.assign({}, target.configuration.registry[foreignKey]);
        }
      } else {
        this.configuration.registry[foreignKey] = Object.assign({}, target.configuration.registry[foreignKey]);
      }
    });
    target._configuration = this.configuration;
    return this;
  }
  /**
   * Execute a patched method
   * @param key The method key
   * @param args Arguments to pass to the method (optional)
   * @see HotPatcher#get
   * @returns The output of the called method
   */
  execute(key) {
    const method = this.get(key) || NOOP;
    for (var _len = arguments.length, args = new Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
      args[_key - 1] = arguments[_key];
    }
    return method(...args);
  }
  /**
   * Get a method for a key
   * @param key The method key
   * @returns Returns the requested function or null if the function
   * does not exist and the host is configured to return null (and not throw)
   * @throws {Error} Throws if the configuration specifies to throw and the method
   * does not exist
   * @throws {Error} Throws if the `getEmptyAction` value is invalid
   */
  get(key) {
    const item = this.configuration.registry[key];
    if (!item) {
      switch (this.getEmptyAction) {
        case "null":
          return null;
        case "throw":
          throw new Error(`Failed handling method request: No method provided for override: ${key}`);
        default:
          throw new Error(`Failed handling request which resulted in an empty method: Invalid empty-action specified: ${this.getEmptyAction}`);
      }
    }
    return sequence(...item.methods);
  }
  /**
   * Check if a method has been patched
   * @param key The function key
   * @returns True if already patched
   */
  isPatched(key) {
    return !!this.configuration.registry[key];
  }
  /**
   * Patch a method name
   * @param key The method key to patch
   * @param method The function to set
   * @param opts Patch options
   * @returns Returns self
   */
  patch(key, method) {
    let opts = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
    const {
      chain = false
    } = opts;
    if (this.configuration.registry[key] && this.configuration.registry[key].final) {
      throw new Error(`Failed patching '${key}': Method marked as being final`);
    }
    if (typeof method !== "function") {
      throw new Error(`Failed patching '${key}': Provided method is not a function`);
    }
    if (chain) {
      // Add new method to the chain
      if (!this.configuration.registry[key]) {
        // New key, create item
        this.configuration.registry[key] = createNewItem(method);
      } else {
        // Existing, push the method
        this.configuration.registry[key].methods.push(method);
      }
    } else {
      // Replace the original
      if (this.isPatched(key)) {
        const {
          original
        } = this.configuration.registry[key];
        this.configuration.registry[key] = Object.assign(createNewItem(method), {
          original
        });
      } else {
        this.configuration.registry[key] = createNewItem(method);
      }
    }
    return this;
  }
  /**
   * Patch a method inline, execute it and return the value
   * Used for patching contents of functions. This method will not apply a patched
   * function if it has already been patched, allowing for external overrides to
   * function. It also means that the function is cached so that it is not
   * instantiated every time the outer function is invoked.
   * @param key The function key to use
   * @param method The function to patch (once, only if not patched)
   * @param args Arguments to pass to the function
   * @returns The output of the patched function
   * @example
   *  function mySpecialFunction(a, b) {
   *      return hotPatcher.patchInline("func", (a, b) => {
   *          return a + b;
   *      }, a, b);
   *  }
   */
  patchInline(key, method) {
    if (!this.isPatched(key)) {
      this.patch(key, method);
    }
    for (var _len2 = arguments.length, args = new Array(_len2 > 2 ? _len2 - 2 : 0), _key2 = 2; _key2 < _len2; _key2++) {
      args[_key2 - 2] = arguments[_key2];
    }
    return this.execute(key, ...args);
  }
  /**
   * Patch a method (or methods) in sequential-mode
   * See `patch()` with the option `chain: true`
   * @see patch
   * @param key The key to patch
   * @param methods The methods to patch
   * @returns Returns self
   */
  plugin(key) {
    for (var _len3 = arguments.length, methods = new Array(_len3 > 1 ? _len3 - 1 : 0), _key3 = 1; _key3 < _len3; _key3++) {
      methods[_key3 - 1] = arguments[_key3];
    }
    methods.forEach(method => {
      this.patch(key, method, {
        chain: true
      });
    });
    return this;
  }
  /**
   * Restore a patched method if it has been overridden
   * @param key The method key
   * @returns Returns self
   */
  restore(key) {
    if (!this.isPatched(key)) {
      throw new Error(`Failed restoring method: No method present for key: ${key}`);
    } else if (typeof this.configuration.registry[key].original !== "function") {
      throw new Error(`Failed restoring method: Original method not found or of invalid type for key: ${key}`);
    }
    this.configuration.registry[key].methods = [this.configuration.registry[key].original];
    return this;
  }
  /**
   * Set a method as being final
   * This sets a method as having been finally overridden. Attempts at overriding
   * again will fail with an error.
   * @param key The key to make final
   * @returns Returns self
   */
  setFinal(key) {
    if (!this.configuration.registry.hasOwnProperty(key)) {
      throw new Error(`Failed marking '${key}' as final: No method found for key`);
    }
    this.configuration.registry[key].final = true;
    return this;
  }
}
;// CONCATENATED MODULE: ./source/compat/patcher.ts

let __patcher = null;
function getPatcher() {
  if (!__patcher) {
    __patcher = new HotPatcher();
  }
  return __patcher;
}
;// CONCATENATED MODULE: ./source/compat/env.ts
function getDebugBuildName() {
  if (true) {
    return "web";
  }
  return "node";
}
function isReactNative() {
  return  true && "web" === "react-native";
}
function env_isWeb() {
  return  true && "web" === "web";
}
;// CONCATENATED MODULE: ./source/tools/merge.ts
function cloneShallow(obj) {
  return isPlainObject(obj) ? Object.assign({}, obj) : Object.setPrototypeOf(Object.assign({}, obj), Object.getPrototypeOf(obj));
}
function isPlainObject(obj) {
  if (typeof obj !== "object" || obj === null || Object.prototype.toString.call(obj) != "[object Object]") {
    // Not an object
    return false;
  }
  if (Object.getPrototypeOf(obj) === null) {
    return true;
  }
  let proto = obj;
  // Find the prototype
  while (Object.getPrototypeOf(proto) !== null) {
    proto = Object.getPrototypeOf(proto);
  }
  return Object.getPrototypeOf(obj) === proto;
}
function merge() {
  for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
    args[_key] = arguments[_key];
  }
  let output = null,
    items = [...args];
  while (items.length > 0) {
    const nextItem = items.shift();
    if (!output) {
      output = cloneShallow(nextItem);
    } else {
      output = mergeObjects(output, nextItem);
    }
  }
  return output;
}
function mergeObjects(obj1, obj2) {
  const output = cloneShallow(obj1);
  Object.keys(obj2).forEach(key => {
    if (!output.hasOwnProperty(key)) {
      output[key] = obj2[key];
      return;
    }
    if (Array.isArray(obj2[key])) {
      output[key] = Array.isArray(output[key]) ? [...output[key], ...obj2[key]] : [...obj2[key]];
    } else if (typeof obj2[key] === "object" && !!obj2[key]) {
      output[key] = typeof output[key] === "object" && !!output[key] ? mergeObjects(output[key], obj2[key]) : cloneShallow(obj2[key]);
    } else {
      output[key] = obj2[key];
    }
  });
  return output;
}
;// CONCATENATED MODULE: ./source/tools/headers.ts
function convertResponseHeaders(headers) {
  const output = {};
  for (const key of headers.keys()) {
    output[key] = headers.get(key);
  }
  return output;
}
function mergeHeaders() {
  for (var _len = arguments.length, headerPayloads = new Array(_len), _key = 0; _key < _len; _key++) {
    headerPayloads[_key] = arguments[_key];
  }
  if (headerPayloads.length === 0) return {};
  const headerKeys = {};
  return headerPayloads.reduce((output, headers) => {
    Object.keys(headers).forEach(header => {
      const lowerHeader = header.toLowerCase();
      if (headerKeys.hasOwnProperty(lowerHeader)) {
        output[headerKeys[lowerHeader]] = headers[header];
      } else {
        headerKeys[lowerHeader] = header;
        output[header] = headers[header];
      }
    });
    return output;
  }, {});
}
// EXTERNAL MODULE: stream (ignored)
var stream_ignored_ = __webpack_require__(805);
var stream_ignored_default = /*#__PURE__*/__webpack_require__.n(stream_ignored_);
;// CONCATENATED MODULE: ./source/compat/arrayBuffer.ts
const hasArrayBuffer = typeof ArrayBuffer === "function";
const {
  toString: objToString
} = Object.prototype;

// Taken from: https://github.com/fengyuanchen/is-array-buffer/blob/master/src/index.js
function isArrayBuffer(value) {
  return hasArrayBuffer && (value instanceof ArrayBuffer || objToString.call(value) === "[object ArrayBuffer]");
}
;// CONCATENATED MODULE: ./source/compat/buffer.ts
function isBuffer(value) {
  return value != null && value.constructor != null && typeof value.constructor.isBuffer === "function" && value.constructor.isBuffer(value);
}
;// CONCATENATED MODULE: ./source/tools/body.ts




function requestDataToFetchBody(data) {
  if (!env_isWeb() && !isReactNative() && data instanceof (stream_ignored_default()).Readable) {
    // @ts-ignore
    return [data, {}];
  }
  if (typeof data === "string") {
    return [data, {}];
  } else if (isBuffer(data)) {
    return [data, {}];
  } else if (isArrayBuffer(data)) {
    return [data, {}];
  } else if (data && typeof data === "object") {
    return [JSON.stringify(data), {
      "content-type": "application/json"
    }];
  }
  throw new Error(`Unable to convert request body: Unexpected body type: ${typeof data}`);
}
;// CONCATENATED MODULE: ./source/request.ts











function _async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
function _await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function _invoke(body, then) {
  var result = body();
  if (result && result.then) {
    return result.then(then);
  }
  return then(result);
}
function getFetchOptions(requestOptions) {
  let headers = {};
  // Handle standard options
  const opts = {
    method: requestOptions.method
  };
  if (requestOptions.headers) {
    headers = mergeHeaders(headers, requestOptions.headers);
  }
  if (typeof requestOptions.data !== "undefined") {
    const [body, newHeaders] = requestDataToFetchBody(requestOptions.data);
    opts.body = body;
    headers = mergeHeaders(headers, newHeaders);
  }
  if (requestOptions.signal) {
    opts.signal = requestOptions.signal;
  }
  if (requestOptions.withCredentials) {
    opts.credentials = "include";
  }
  // Check for node-specific options
  if (!env_isWeb() && !isReactNative()) {
    if (requestOptions.httpAgent || requestOptions.httpsAgent) {
      opts.agent = parsedURL => {
        if (parsedURL.protocol === "http:") {
          return requestOptions.httpAgent || new http_ignored_.Agent();
        }
        return requestOptions.httpsAgent || new https_ignored_.Agent();
      };
    }
  }
  // Attach headers
  opts.headers = headers;
  return opts;
}
const requestDigest = _async(function (requestOptions) {
  // Remove client's digest authentication object from request options
  const _digest = requestOptions._digest;
  delete requestOptions._digest;
  // If client is already using digest authentication, include the digest authorization header
  if (_digest.hasDigestAuth) {
    requestOptions = merge(requestOptions, {
      headers: {
        Authorization: generateDigestAuthHeader(requestOptions, _digest)
      }
    });
  }
  // Perform digest request + check
  return _await(requestStandard(requestOptions), function (response) {
    let _exit = false;
    return _invoke(function () {
      if (response.status == 401) {
        _digest.hasDigestAuth = parseDigestAuth(response, _digest);
        return function () {
          if (_digest.hasDigestAuth) {
            requestOptions = merge(requestOptions, {
              headers: {
                Authorization: generateDigestAuthHeader(requestOptions, _digest)
              }
            });
            return _await(requestStandard(requestOptions), function (response2) {
              if (response2.status == 401) {
                _digest.hasDigestAuth = false;
              } else {
                _digest.nc++;
              }
              _exit = true;
              return response2;
            });
          }
        }();
      } else {
        _digest.nc++;
      }
    }, function (_result2) {
      return _exit ? _result2 : response;
    });
  });
});
const requestAuto = _async(function (requestOptions, context) {
  return _await(requestStandard(requestOptions), function (response) {
    if (response.ok) {
      context.authType = AuthType.Password;
      return response;
    }
    if (response.status == 401 && responseIndicatesDigestAuth(response)) {
      context.authType = AuthType.Digest;
      setupAuth(context, context.username, context.password, undefined, undefined);
      requestOptions._digest = context.digest;
      return requestDigest(requestOptions);
    }
    return response;
  });
});
const request = _async(function (requestOptions, context) {
  if (context.authType === AuthType.Auto) {
    return requestAuto(requestOptions, context);
  }
  return requestOptions._digest ? requestDigest(requestOptions) : requestStandard(requestOptions);
});
function prepareRequestOptions(requestOptions, context, userOptions) {
  const finalOptions = cloneShallow(requestOptions);
  finalOptions.headers = mergeHeaders(context.headers, finalOptions.headers || {}, userOptions.headers || {});
  if (typeof userOptions.data !== "undefined") {
    finalOptions.data = userOptions.data;
  }
  if (userOptions.signal) {
    finalOptions.signal = userOptions.signal;
  }
  if (context.httpAgent) {
    finalOptions.httpAgent = context.httpAgent;
  }
  if (context.httpsAgent) {
    finalOptions.httpsAgent = context.httpsAgent;
  }
  if (context.digest) {
    finalOptions._digest = context.digest;
  }
  if (typeof context.withCredentials === "boolean") {
    finalOptions.withCredentials = context.withCredentials;
  }
  return finalOptions;
}
function requestStandard(requestOptions) {
  const patcher = getPatcher();
  return patcher.patchInline("request", options => patcher.patchInline("fetch", fetch, options.url, getFetchOptions(options)), requestOptions);
}
// EXTERNAL MODULE: ./node_modules/minimatch/node_modules/brace-expansion/index.js
var brace_expansion = __webpack_require__(285);
;// CONCATENATED MODULE: ./node_modules/minimatch/dist/esm/assert-valid-pattern.js
const MAX_PATTERN_LENGTH = 1024 * 64;
const assertValidPattern = pattern => {
  if (typeof pattern !== 'string') {
    throw new TypeError('invalid pattern');
  }
  if (pattern.length > MAX_PATTERN_LENGTH) {
    throw new TypeError('pattern is too long');
  }
};
;// CONCATENATED MODULE: ./node_modules/minimatch/dist/esm/brace-expressions.js
// translate the various posix character classes into unicode properties
// this works across all unicode locales
// { <posix class>: [<translation>, /u flag required, negated]
const posixClasses = {
  '[:alnum:]': ['\\p{L}\\p{Nl}\\p{Nd}', true],
  '[:alpha:]': ['\\p{L}\\p{Nl}', true],
  '[:ascii:]': ['\\x' + '00-\\x' + '7f', false],
  '[:blank:]': ['\\p{Zs}\\t', true],
  '[:cntrl:]': ['\\p{Cc}', true],
  '[:digit:]': ['\\p{Nd}', true],
  '[:graph:]': ['\\p{Z}\\p{C}', true, true],
  '[:lower:]': ['\\p{Ll}', true],
  '[:print:]': ['\\p{C}', true],
  '[:punct:]': ['\\p{P}', true],
  '[:space:]': ['\\p{Z}\\t\\r\\n\\v\\f', true],
  '[:upper:]': ['\\p{Lu}', true],
  '[:word:]': ['\\p{L}\\p{Nl}\\p{Nd}\\p{Pc}', true],
  '[:xdigit:]': ['A-Fa-f0-9', false]
};
// only need to escape a few things inside of brace expressions
// escapes: [ \ ] -
const braceEscape = s => s.replace(/[[\]\\-]/g, '\\$&');
// escape all regexp magic characters
const regexpEscape = s => s.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
// everything has already been escaped, we just have to join
const rangesToString = ranges => ranges.join('');
// takes a glob string at a posix brace expression, and returns
// an equivalent regular expression source, and boolean indicating
// whether the /u flag needs to be applied, and the number of chars
// consumed to parse the character class.
// This also removes out of order ranges, and returns ($.) if the
// entire class just no good.
const parseClass = (glob, position) => {
  const pos = position;
  /* c8 ignore start */
  if (glob.charAt(pos) !== '[') {
    throw new Error('not in a brace expression');
  }
  /* c8 ignore stop */
  const ranges = [];
  const negs = [];
  let i = pos + 1;
  let sawStart = false;
  let uflag = false;
  let escaping = false;
  let negate = false;
  let endPos = pos;
  let rangeStart = '';
  WHILE: while (i < glob.length) {
    const c = glob.charAt(i);
    if ((c === '!' || c === '^') && i === pos + 1) {
      negate = true;
      i++;
      continue;
    }
    if (c === ']' && sawStart && !escaping) {
      endPos = i + 1;
      break;
    }
    sawStart = true;
    if (c === '\\') {
      if (!escaping) {
        escaping = true;
        i++;
        continue;
      }
      // escaped \ char, fall through and treat like normal char
    }
    if (c === '[' && !escaping) {
      // either a posix class, a collation equivalent, or just a [
      for (const [cls, [unip, u, neg]] of Object.entries(posixClasses)) {
        if (glob.startsWith(cls, i)) {
          // invalid, [a-[] is fine, but not [a-[:alpha]]
          if (rangeStart) {
            return ['$.', false, glob.length - pos, true];
          }
          i += cls.length;
          if (neg) negs.push(unip);else ranges.push(unip);
          uflag = uflag || u;
          continue WHILE;
        }
      }
    }
    // now it's just a normal character, effectively
    escaping = false;
    if (rangeStart) {
      // throw this range away if it's not valid, but others
      // can still match.
      if (c > rangeStart) {
        ranges.push(braceEscape(rangeStart) + '-' + braceEscape(c));
      } else if (c === rangeStart) {
        ranges.push(braceEscape(c));
      }
      rangeStart = '';
      i++;
      continue;
    }
    // now might be the start of a range.
    // can be either c-d or c-] or c<more...>] or c] at this point
    if (glob.startsWith('-]', i + 1)) {
      ranges.push(braceEscape(c + '-'));
      i += 2;
      continue;
    }
    if (glob.startsWith('-', i + 1)) {
      rangeStart = c;
      i += 2;
      continue;
    }
    // not the start of a range, just a single character
    ranges.push(braceEscape(c));
    i++;
  }
  if (endPos < i) {
    // didn't see the end of the class, not a valid class,
    // but might still be valid as a literal match.
    return ['', false, 0, false];
  }
  // if we got no ranges and no negates, then we have a range that
  // cannot possibly match anything, and that poisons the whole glob
  if (!ranges.length && !negs.length) {
    return ['$.', false, glob.length - pos, true];
  }
  // if we got one positive range, and it's a single character, then that's
  // not actually a magic pattern, it's just that one literal character.
  // we should not treat that as "magic", we should just return the literal
  // character. [_] is a perfectly valid way to escape glob magic chars.
  if (negs.length === 0 && ranges.length === 1 && /^\\?.$/.test(ranges[0]) && !negate) {
    const r = ranges[0].length === 2 ? ranges[0].slice(-1) : ranges[0];
    return [regexpEscape(r), false, endPos - pos, false];
  }
  const sranges = '[' + (negate ? '^' : '') + rangesToString(ranges) + ']';
  const snegs = '[' + (negate ? '' : '^') + rangesToString(negs) + ']';
  const comb = ranges.length && negs.length ? '(' + sranges + '|' + snegs + ')' : ranges.length ? sranges : snegs;
  return [comb, uflag, endPos - pos, true];
};
;// CONCATENATED MODULE: ./node_modules/minimatch/dist/esm/unescape.js
/**
 * Un-escape a string that has been escaped with {@link escape}.
 *
 * If the {@link windowsPathsNoEscape} option is used, then square-brace
 * escapes are removed, but not backslash escapes.  For example, it will turn
 * the string `'[*]'` into `*`, but it will not turn `'\\*'` into `'*'`,
 * becuase `\` is a path separator in `windowsPathsNoEscape` mode.
 *
 * When `windowsPathsNoEscape` is not set, then both brace escapes and
 * backslash escapes are removed.
 *
 * Slashes (and backslashes in `windowsPathsNoEscape` mode) cannot be escaped
 * or unescaped.
 */
const unescape_unescape = function (s) {
  let {
    windowsPathsNoEscape = false
  } = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  return windowsPathsNoEscape ? s.replace(/\[([^\/\\])\]/g, '$1') : s.replace(/((?!\\).|^)\[([^\/\\])\]/g, '$1$2').replace(/\\([^\/])/g, '$1');
};
;// CONCATENATED MODULE: ./node_modules/minimatch/dist/esm/ast.js
// parse a single path portion


const types = new Set(['!', '?', '+', '*', '@']);
const isExtglobType = c => types.has(c);
// Patterns that get prepended to bind to the start of either the
// entire string, or just a single path portion, to prevent dots
// and/or traversal patterns, when needed.
// Exts don't need the ^ or / bit, because the root binds that already.
const startNoTraversal = '(?!(?:^|/)\\.\\.?(?:$|/))';
const startNoDot = '(?!\\.)';
// characters that indicate a start of pattern needs the "no dots" bit,
// because a dot *might* be matched. ( is not in the list, because in
// the case of a child extglob, it will handle the prevention itself.
const addPatternStart = new Set(['[', '.']);
// cases where traversal is A-OK, no dot prevention needed
const justDots = new Set(['..', '.']);
const reSpecials = new Set('().*{}+?[]^$\\!');
const regExpEscape = s => s.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
// any single thing other than /
const qmark = '[^/]';
// * => any number of characters
const star = qmark + '*?';
// use + when we need to ensure that *something* matches, because the * is
// the only thing in the path portion.
const starNoEmpty = qmark + '+?';
// remove the \ chars that we added if we end up doing a nonmagic compare
// const deslash = (s: string) => s.replace(/\\(.)/g, '$1')
class AST {
  type;
  #root;
  #hasMagic;
  #uflag = false;
  #parts = [];
  #parent;
  #parentIndex;
  #negs;
  #filledNegs = false;
  #options;
  #toString;
  // set to true if it's an extglob with no children
  // (which really means one child of '')
  #emptyExt = false;
  constructor(type, parent) {
    let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
    this.type = type;
    // extglobs are inherently magical
    if (type) this.#hasMagic = true;
    this.#parent = parent;
    this.#root = this.#parent ? this.#parent.#root : this;
    this.#options = this.#root === this ? options : this.#root.#options;
    this.#negs = this.#root === this ? [] : this.#root.#negs;
    if (type === '!' && !this.#root.#filledNegs) this.#negs.push(this);
    this.#parentIndex = this.#parent ? this.#parent.#parts.length : 0;
  }
  get hasMagic() {
    /* c8 ignore start */
    if (this.#hasMagic !== undefined) return this.#hasMagic;
    /* c8 ignore stop */
    for (const p of this.#parts) {
      if (typeof p === 'string') continue;
      if (p.type || p.hasMagic) return this.#hasMagic = true;
    }
    // note: will be undefined until we generate the regexp src and find out
    return this.#hasMagic;
  }
  // reconstructs the pattern
  toString() {
    if (this.#toString !== undefined) return this.#toString;
    if (!this.type) {
      return this.#toString = this.#parts.map(p => String(p)).join('');
    } else {
      return this.#toString = this.type + '(' + this.#parts.map(p => String(p)).join('|') + ')';
    }
  }
  #fillNegs() {
    /* c8 ignore start */
    if (this !== this.#root) throw new Error('should only call on root');
    if (this.#filledNegs) return this;
    /* c8 ignore stop */
    // call toString() once to fill this out
    this.toString();
    this.#filledNegs = true;
    let n;
    while (n = this.#negs.pop()) {
      if (n.type !== '!') continue;
      // walk up the tree, appending everthing that comes AFTER parentIndex
      let p = n;
      let pp = p.#parent;
      while (pp) {
        for (let i = p.#parentIndex + 1; !pp.type && i < pp.#parts.length; i++) {
          for (const part of n.#parts) {
            /* c8 ignore start */
            if (typeof part === 'string') {
              throw new Error('string part in extglob AST??');
            }
            /* c8 ignore stop */
            part.copyIn(pp.#parts[i]);
          }
        }
        p = pp;
        pp = p.#parent;
      }
    }
    return this;
  }
  push() {
    for (var _len = arguments.length, parts = new Array(_len), _key = 0; _key < _len; _key++) {
      parts[_key] = arguments[_key];
    }
    for (const p of parts) {
      if (p === '') continue;
      /* c8 ignore start */
      if (typeof p !== 'string' && !(p instanceof AST && p.#parent === this)) {
        throw new Error('invalid part: ' + p);
      }
      /* c8 ignore stop */
      this.#parts.push(p);
    }
  }
  toJSON() {
    const ret = this.type === null ? this.#parts.slice().map(p => typeof p === 'string' ? p : p.toJSON()) : [this.type, ...this.#parts.map(p => p.toJSON())];
    if (this.isStart() && !this.type) ret.unshift([]);
    if (this.isEnd() && (this === this.#root || this.#root.#filledNegs && this.#parent?.type === '!')) {
      ret.push({});
    }
    return ret;
  }
  isStart() {
    if (this.#root === this) return true;
    // if (this.type) return !!this.#parent?.isStart()
    if (!this.#parent?.isStart()) return false;
    if (this.#parentIndex === 0) return true;
    // if everything AHEAD of this is a negation, then it's still the "start"
    const p = this.#parent;
    for (let i = 0; i < this.#parentIndex; i++) {
      const pp = p.#parts[i];
      if (!(pp instanceof AST && pp.type === '!')) {
        return false;
      }
    }
    return true;
  }
  isEnd() {
    if (this.#root === this) return true;
    if (this.#parent?.type === '!') return true;
    if (!this.#parent?.isEnd()) return false;
    if (!this.type) return this.#parent?.isEnd();
    // if not root, it'll always have a parent
    /* c8 ignore start */
    const pl = this.#parent ? this.#parent.#parts.length : 0;
    /* c8 ignore stop */
    return this.#parentIndex === pl - 1;
  }
  copyIn(part) {
    if (typeof part === 'string') this.push(part);else this.push(part.clone(this));
  }
  clone(parent) {
    const c = new AST(this.type, parent);
    for (const p of this.#parts) {
      c.copyIn(p);
    }
    return c;
  }
  static #parseAST(str, ast, pos, opt) {
    let escaping = false;
    let inBrace = false;
    let braceStart = -1;
    let braceNeg = false;
    if (ast.type === null) {
      // outside of a extglob, append until we find a start
      let i = pos;
      let acc = '';
      while (i < str.length) {
        const c = str.charAt(i++);
        // still accumulate escapes at this point, but we do ignore
        // starts that are escaped
        if (escaping || c === '\\') {
          escaping = !escaping;
          acc += c;
          continue;
        }
        if (inBrace) {
          if (i === braceStart + 1) {
            if (c === '^' || c === '!') {
              braceNeg = true;
            }
          } else if (c === ']' && !(i === braceStart + 2 && braceNeg)) {
            inBrace = false;
          }
          acc += c;
          continue;
        } else if (c === '[') {
          inBrace = true;
          braceStart = i;
          braceNeg = false;
          acc += c;
          continue;
        }
        if (!opt.noext && isExtglobType(c) && str.charAt(i) === '(') {
          ast.push(acc);
          acc = '';
          const ext = new AST(c, ast);
          i = AST.#parseAST(str, ext, i, opt);
          ast.push(ext);
          continue;
        }
        acc += c;
      }
      ast.push(acc);
      return i;
    }
    // some kind of extglob, pos is at the (
    // find the next | or )
    let i = pos + 1;
    let part = new AST(null, ast);
    const parts = [];
    let acc = '';
    while (i < str.length) {
      const c = str.charAt(i++);
      // still accumulate escapes at this point, but we do ignore
      // starts that are escaped
      if (escaping || c === '\\') {
        escaping = !escaping;
        acc += c;
        continue;
      }
      if (inBrace) {
        if (i === braceStart + 1) {
          if (c === '^' || c === '!') {
            braceNeg = true;
          }
        } else if (c === ']' && !(i === braceStart + 2 && braceNeg)) {
          inBrace = false;
        }
        acc += c;
        continue;
      } else if (c === '[') {
        inBrace = true;
        braceStart = i;
        braceNeg = false;
        acc += c;
        continue;
      }
      if (isExtglobType(c) && str.charAt(i) === '(') {
        part.push(acc);
        acc = '';
        const ext = new AST(c, part);
        part.push(ext);
        i = AST.#parseAST(str, ext, i, opt);
        continue;
      }
      if (c === '|') {
        part.push(acc);
        acc = '';
        parts.push(part);
        part = new AST(null, ast);
        continue;
      }
      if (c === ')') {
        if (acc === '' && ast.#parts.length === 0) {
          ast.#emptyExt = true;
        }
        part.push(acc);
        acc = '';
        ast.push(...parts, part);
        return i;
      }
      acc += c;
    }
    // unfinished extglob
    // if we got here, it was a malformed extglob! not an extglob, but
    // maybe something else in there.
    ast.type = null;
    ast.#hasMagic = undefined;
    ast.#parts = [str.substring(pos - 1)];
    return i;
  }
  static fromGlob(pattern) {
    let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
    const ast = new AST(null, undefined, options);
    AST.#parseAST(pattern, ast, 0, options);
    return ast;
  }
  // returns the regular expression if there's magic, or the unescaped
  // string if not.
  toMMPattern() {
    // should only be called on root
    /* c8 ignore start */
    if (this !== this.#root) return this.#root.toMMPattern();
    /* c8 ignore stop */
    const glob = this.toString();
    const [re, body, hasMagic, uflag] = this.toRegExpSource();
    // if we're in nocase mode, and not nocaseMagicOnly, then we do
    // still need a regular expression if we have to case-insensitively
    // match capital/lowercase characters.
    const anyMagic = hasMagic || this.#hasMagic || this.#options.nocase && !this.#options.nocaseMagicOnly && glob.toUpperCase() !== glob.toLowerCase();
    if (!anyMagic) {
      return body;
    }
    const flags = (this.#options.nocase ? 'i' : '') + (uflag ? 'u' : '');
    return Object.assign(new RegExp(`^${re}$`, flags), {
      _src: re,
      _glob: glob
    });
  }
  get options() {
    return this.#options;
  }
  // returns the string match, the regexp source, whether there's magic
  // in the regexp (so a regular expression is required) and whether or
  // not the uflag is needed for the regular expression (for posix classes)
  // TODO: instead of injecting the start/end at this point, just return
  // the BODY of the regexp, along with the start/end portions suitable
  // for binding the start/end in either a joined full-path makeRe context
  // (where we bind to (^|/), or a standalone matchPart context (where
  // we bind to ^, and not /).  Otherwise slashes get duped!
  //
  // In part-matching mode, the start is:
  // - if not isStart: nothing
  // - if traversal possible, but not allowed: ^(?!\.\.?$)
  // - if dots allowed or not possible: ^
  // - if dots possible and not allowed: ^(?!\.)
  // end is:
  // - if not isEnd(): nothing
  // - else: $
  //
  // In full-path matching mode, we put the slash at the START of the
  // pattern, so start is:
  // - if first pattern: same as part-matching mode
  // - if not isStart(): nothing
  // - if traversal possible, but not allowed: /(?!\.\.?(?:$|/))
  // - if dots allowed or not possible: /
  // - if dots possible and not allowed: /(?!\.)
  // end is:
  // - if last pattern, same as part-matching mode
  // - else nothing
  //
  // Always put the (?:$|/) on negated tails, though, because that has to be
  // there to bind the end of the negated pattern portion, and it's easier to
  // just stick it in now rather than try to inject it later in the middle of
  // the pattern.
  //
  // We can just always return the same end, and leave it up to the caller
  // to know whether it's going to be used joined or in parts.
  // And, if the start is adjusted slightly, can do the same there:
  // - if not isStart: nothing
  // - if traversal possible, but not allowed: (?:/|^)(?!\.\.?$)
  // - if dots allowed or not possible: (?:/|^)
  // - if dots possible and not allowed: (?:/|^)(?!\.)
  //
  // But it's better to have a simpler binding without a conditional, for
  // performance, so probably better to return both start options.
  //
  // Then the caller just ignores the end if it's not the first pattern,
  // and the start always gets applied.
  //
  // But that's always going to be $ if it's the ending pattern, or nothing,
  // so the caller can just attach $ at the end of the pattern when building.
  //
  // So the todo is:
  // - better detect what kind of start is needed
  // - return both flavors of starting pattern
  // - attach $ at the end of the pattern when creating the actual RegExp
  //
  // Ah, but wait, no, that all only applies to the root when the first pattern
  // is not an extglob. If the first pattern IS an extglob, then we need all
  // that dot prevention biz to live in the extglob portions, because eg
  // +(*|.x*) can match .xy but not .yx.
  //
  // So, return the two flavors if it's #root and the first child is not an
  // AST, otherwise leave it to the child AST to handle it, and there,
  // use the (?:^|/) style of start binding.
  //
  // Even simplified further:
  // - Since the start for a join is eg /(?!\.) and the start for a part
  // is ^(?!\.), we can just prepend (?!\.) to the pattern (either root
  // or start or whatever) and prepend ^ or / at the Regexp construction.
  toRegExpSource(allowDot) {
    const dot = allowDot ?? !!this.#options.dot;
    if (this.#root === this) this.#fillNegs();
    if (!this.type) {
      const noEmpty = this.isStart() && this.isEnd();
      const src = this.#parts.map(p => {
        const [re, _, hasMagic, uflag] = typeof p === 'string' ? AST.#parseGlob(p, this.#hasMagic, noEmpty) : p.toRegExpSource(allowDot);
        this.#hasMagic = this.#hasMagic || hasMagic;
        this.#uflag = this.#uflag || uflag;
        return re;
      }).join('');
      let start = '';
      if (this.isStart()) {
        if (typeof this.#parts[0] === 'string') {
          // this is the string that will match the start of the pattern,
          // so we need to protect against dots and such.
          // '.' and '..' cannot match unless the pattern is that exactly,
          // even if it starts with . or dot:true is set.
          const dotTravAllowed = this.#parts.length === 1 && justDots.has(this.#parts[0]);
          if (!dotTravAllowed) {
            const aps = addPatternStart;
            // check if we have a possibility of matching . or ..,
            // and prevent that.
            const needNoTrav =
            // dots are allowed, and the pattern starts with [ or .
            dot && aps.has(src.charAt(0)) ||
            // the pattern starts with \., and then [ or .
            src.startsWith('\\.') && aps.has(src.charAt(2)) ||
            // the pattern starts with \.\., and then [ or .
            src.startsWith('\\.\\.') && aps.has(src.charAt(4));
            // no need to prevent dots if it can't match a dot, or if a
            // sub-pattern will be preventing it anyway.
            const needNoDot = !dot && !allowDot && aps.has(src.charAt(0));
            start = needNoTrav ? startNoTraversal : needNoDot ? startNoDot : '';
          }
        }
      }
      // append the "end of path portion" pattern to negation tails
      let end = '';
      if (this.isEnd() && this.#root.#filledNegs && this.#parent?.type === '!') {
        end = '(?:$|\\/)';
      }
      const final = start + src + end;
      return [final, unescape_unescape(src), this.#hasMagic = !!this.#hasMagic, this.#uflag];
    }
    // We need to calculate the body *twice* if it's a repeat pattern
    // at the start, once in nodot mode, then again in dot mode, so a
    // pattern like *(?) can match 'x.y'
    const repeated = this.type === '*' || this.type === '+';
    // some kind of extglob
    const start = this.type === '!' ? '(?:(?!(?:' : '(?:';
    let body = this.#partsToRegExp(dot);
    if (this.isStart() && this.isEnd() && !body && this.type !== '!') {
      // invalid extglob, has to at least be *something* present, if it's
      // the entire path portion.
      const s = this.toString();
      this.#parts = [s];
      this.type = null;
      this.#hasMagic = undefined;
      return [s, unescape_unescape(this.toString()), false, false];
    }
    // XXX abstract out this map method
    let bodyDotAllowed = !repeated || allowDot || dot || !startNoDot ? '' : this.#partsToRegExp(true);
    if (bodyDotAllowed === body) {
      bodyDotAllowed = '';
    }
    if (bodyDotAllowed) {
      body = `(?:${body})(?:${bodyDotAllowed})*?`;
    }
    // an empty !() is exactly equivalent to a starNoEmpty
    let final = '';
    if (this.type === '!' && this.#emptyExt) {
      final = (this.isStart() && !dot ? startNoDot : '') + starNoEmpty;
    } else {
      const close = this.type === '!' ?
      // !() must match something,but !(x) can match ''
      '))' + (this.isStart() && !dot && !allowDot ? startNoDot : '') + star + ')' : this.type === '@' ? ')' : this.type === '?' ? ')?' : this.type === '+' && bodyDotAllowed ? ')' : this.type === '*' && bodyDotAllowed ? `)?` : `)${this.type}`;
      final = start + body + close;
    }
    return [final, unescape_unescape(body), this.#hasMagic = !!this.#hasMagic, this.#uflag];
  }
  #partsToRegExp(dot) {
    return this.#parts.map(p => {
      // extglob ASTs should only contain parent ASTs
      /* c8 ignore start */
      if (typeof p === 'string') {
        throw new Error('string type in extglob ast??');
      }
      /* c8 ignore stop */
      // can ignore hasMagic, because extglobs are already always magic
      const [re, _, _hasMagic, uflag] = p.toRegExpSource(dot);
      this.#uflag = this.#uflag || uflag;
      return re;
    }).filter(p => !(this.isStart() && this.isEnd()) || !!p).join('|');
  }
  static #parseGlob(glob, hasMagic) {
    let noEmpty = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : false;
    let escaping = false;
    let re = '';
    let uflag = false;
    for (let i = 0; i < glob.length; i++) {
      const c = glob.charAt(i);
      if (escaping) {
        escaping = false;
        re += (reSpecials.has(c) ? '\\' : '') + c;
        continue;
      }
      if (c === '\\') {
        if (i === glob.length - 1) {
          re += '\\\\';
        } else {
          escaping = true;
        }
        continue;
      }
      if (c === '[') {
        const [src, needUflag, consumed, magic] = parseClass(glob, i);
        if (consumed) {
          re += src;
          uflag = uflag || needUflag;
          i += consumed - 1;
          hasMagic = hasMagic || magic;
          continue;
        }
      }
      if (c === '*') {
        if (noEmpty && glob === '*') re += starNoEmpty;else re += star;
        hasMagic = true;
        continue;
      }
      if (c === '?') {
        re += qmark;
        hasMagic = true;
        continue;
      }
      re += regExpEscape(c);
    }
    return [re, unescape_unescape(glob), !!hasMagic, uflag];
  }
}
;// CONCATENATED MODULE: ./node_modules/minimatch/dist/esm/escape.js
/**
 * Escape all magic characters in a glob pattern.
 *
 * If the {@link windowsPathsNoEscape | GlobOptions.windowsPathsNoEscape}
 * option is used, then characters are escaped by wrapping in `[]`, because
 * a magic character wrapped in a character class can only be satisfied by
 * that exact character.  In this mode, `\` is _not_ escaped, because it is
 * not interpreted as a magic character, but instead as a path separator.
 */
const escape_escape = function (s) {
  let {
    windowsPathsNoEscape = false
  } = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  // don't need to escape +@! because we escape the parens
  // that make those magic, and escaping ! as [!] isn't valid,
  // because [!]] is a valid glob class meaning not ']'.
  return windowsPathsNoEscape ? s.replace(/[?*()[\]]/g, '[$&]') : s.replace(/[?*()[\]\\]/g, '\\$&');
};
;// CONCATENATED MODULE: ./node_modules/minimatch/dist/esm/index.js





const minimatch = function (p, pattern) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  assertValidPattern(pattern);
  // shortcut: comments match nothing.
  if (!options.nocomment && pattern.charAt(0) === '#') {
    return false;
  }
  return new Minimatch(pattern, options).match(p);
};
// Optimized checking for the most common glob patterns.
const starDotExtRE = /^\*+([^+@!?\*\[\(]*)$/;
const starDotExtTest = ext => f => !f.startsWith('.') && f.endsWith(ext);
const starDotExtTestDot = ext => f => f.endsWith(ext);
const starDotExtTestNocase = ext => {
  ext = ext.toLowerCase();
  return f => !f.startsWith('.') && f.toLowerCase().endsWith(ext);
};
const starDotExtTestNocaseDot = ext => {
  ext = ext.toLowerCase();
  return f => f.toLowerCase().endsWith(ext);
};
const starDotStarRE = /^\*+\.\*+$/;
const starDotStarTest = f => !f.startsWith('.') && f.includes('.');
const starDotStarTestDot = f => f !== '.' && f !== '..' && f.includes('.');
const dotStarRE = /^\.\*+$/;
const dotStarTest = f => f !== '.' && f !== '..' && f.startsWith('.');
const starRE = /^\*+$/;
const starTest = f => f.length !== 0 && !f.startsWith('.');
const starTestDot = f => f.length !== 0 && f !== '.' && f !== '..';
const qmarksRE = /^\?+([^+@!?\*\[\(]*)?$/;
const qmarksTestNocase = _ref => {
  let [$0, ext = ''] = _ref;
  const noext = qmarksTestNoExt([$0]);
  if (!ext) return noext;
  ext = ext.toLowerCase();
  return f => noext(f) && f.toLowerCase().endsWith(ext);
};
const qmarksTestNocaseDot = _ref2 => {
  let [$0, ext = ''] = _ref2;
  const noext = qmarksTestNoExtDot([$0]);
  if (!ext) return noext;
  ext = ext.toLowerCase();
  return f => noext(f) && f.toLowerCase().endsWith(ext);
};
const qmarksTestDot = _ref3 => {
  let [$0, ext = ''] = _ref3;
  const noext = qmarksTestNoExtDot([$0]);
  return !ext ? noext : f => noext(f) && f.endsWith(ext);
};
const qmarksTest = _ref4 => {
  let [$0, ext = ''] = _ref4;
  const noext = qmarksTestNoExt([$0]);
  return !ext ? noext : f => noext(f) && f.endsWith(ext);
};
const qmarksTestNoExt = _ref5 => {
  let [$0] = _ref5;
  const len = $0.length;
  return f => f.length === len && !f.startsWith('.');
};
const qmarksTestNoExtDot = _ref6 => {
  let [$0] = _ref6;
  const len = $0.length;
  return f => f.length === len && f !== '.' && f !== '..';
};
/* c8 ignore start */
const defaultPlatform = typeof process === 'object' && process ? typeof process.env === 'object' && process.env && process.env.__MINIMATCH_TESTING_PLATFORM__ || process.platform : 'posix';
const path = {
  win32: {
    sep: '\\'
  },
  posix: {
    sep: '/'
  }
};
/* c8 ignore stop */
const sep = defaultPlatform === 'win32' ? path.win32.sep : path.posix.sep;
minimatch.sep = sep;
const GLOBSTAR = Symbol('globstar **');
minimatch.GLOBSTAR = GLOBSTAR;
// any single thing other than /
// don't need to escape / when using new RegExp()
const esm_qmark = '[^/]';
// * => any number of characters
const esm_star = esm_qmark + '*?';
// ** when dots are allowed.  Anything goes, except .. and .
// not (^ or / followed by one or two dots followed by $ or /),
// followed by anything, any number of times.
const twoStarDot = '(?:(?!(?:\\/|^)(?:\\.{1,2})($|\\/)).)*?';
// not a ^ or / followed by a dot,
// followed by anything, any number of times.
const twoStarNoDot = '(?:(?!(?:\\/|^)\\.).)*?';
const filter = function (pattern) {
  let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  return p => minimatch(p, pattern, options);
};
minimatch.filter = filter;
const ext = function (a) {
  let b = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  return Object.assign({}, a, b);
};
const defaults = def => {
  if (!def || typeof def !== 'object' || !Object.keys(def).length) {
    return minimatch;
  }
  const orig = minimatch;
  const m = function (p, pattern) {
    let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
    return orig(p, pattern, ext(def, options));
  };
  return Object.assign(m, {
    Minimatch: class Minimatch extends orig.Minimatch {
      constructor(pattern) {
        let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
        super(pattern, ext(def, options));
      }
      static defaults(options) {
        return orig.defaults(ext(def, options)).Minimatch;
      }
    },
    AST: class AST extends orig.AST {
      /* c8 ignore start */
      constructor(type, parent) {
        let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
        super(type, parent, ext(def, options));
      }
      /* c8 ignore stop */
      static fromGlob(pattern) {
        let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
        return orig.AST.fromGlob(pattern, ext(def, options));
      }
    },
    unescape: function (s) {
      let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
      return orig.unescape(s, ext(def, options));
    },
    escape: function (s) {
      let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
      return orig.escape(s, ext(def, options));
    },
    filter: function (pattern) {
      let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
      return orig.filter(pattern, ext(def, options));
    },
    defaults: options => orig.defaults(ext(def, options)),
    makeRe: function (pattern) {
      let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
      return orig.makeRe(pattern, ext(def, options));
    },
    braceExpand: function (pattern) {
      let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
      return orig.braceExpand(pattern, ext(def, options));
    },
    match: function (list, pattern) {
      let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
      return orig.match(list, pattern, ext(def, options));
    },
    sep: orig.sep,
    GLOBSTAR: GLOBSTAR
  });
};
minimatch.defaults = defaults;
// Brace expansion:
// a{b,c}d -> abd acd
// a{b,}c -> abc ac
// a{0..3}d -> a0d a1d a2d a3d
// a{b,c{d,e}f}g -> abg acdfg acefg
// a{b,c}d{e,f}g -> abdeg acdeg abdeg abdfg
//
// Invalid sets are not expanded.
// a{2..}b -> a{2..}b
// a{b}c -> a{b}c
const braceExpand = function (pattern) {
  let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  assertValidPattern(pattern);
  // Thanks to Yeting Li <https://github.com/yetingli> for
  // improving this regexp to avoid a ReDOS vulnerability.
  if (options.nobrace || !/\{(?:(?!\{).)*\}/.test(pattern)) {
    // shortcut. no need to expand.
    return [pattern];
  }
  return brace_expansion(pattern);
};
minimatch.braceExpand = braceExpand;
// parse a component of the expanded set.
// At this point, no pattern may contain "/" in it
// so we're going to return a 2d array, where each entry is the full
// pattern, split on '/', and then turned into a regular expression.
// A regexp is made at the end which joins each array with an
// escaped /, and another full one which joins each regexp with |.
//
// Following the lead of Bash 4.1, note that "**" only has special meaning
// when it is the *only* thing in a path portion.  Otherwise, any series
// of * is equivalent to a single *.  Globstar behavior is enabled by
// default, and can be disabled by setting options.noglobstar.
const makeRe = function (pattern) {
  let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  return new Minimatch(pattern, options).makeRe();
};
minimatch.makeRe = makeRe;
const match = function (list, pattern) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const mm = new Minimatch(pattern, options);
  list = list.filter(f => mm.match(f));
  if (mm.options.nonull && !list.length) {
    list.push(pattern);
  }
  return list;
};
minimatch.match = match;
// replace stuff like \* with *
const globMagic = /[?*]|[+@!]\(.*?\)|\[|\]/;
const esm_regExpEscape = s => s.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
class Minimatch {
  options;
  set;
  pattern;
  windowsPathsNoEscape;
  nonegate;
  negate;
  comment;
  empty;
  preserveMultipleSlashes;
  partial;
  globSet;
  globParts;
  nocase;
  isWindows;
  platform;
  windowsNoMagicRoot;
  regexp;
  constructor(pattern) {
    let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
    assertValidPattern(pattern);
    options = options || {};
    this.options = options;
    this.pattern = pattern;
    this.platform = options.platform || defaultPlatform;
    this.isWindows = this.platform === 'win32';
    this.windowsPathsNoEscape = !!options.windowsPathsNoEscape || options.allowWindowsEscape === false;
    if (this.windowsPathsNoEscape) {
      this.pattern = this.pattern.replace(/\\/g, '/');
    }
    this.preserveMultipleSlashes = !!options.preserveMultipleSlashes;
    this.regexp = null;
    this.negate = false;
    this.nonegate = !!options.nonegate;
    this.comment = false;
    this.empty = false;
    this.partial = !!options.partial;
    this.nocase = !!this.options.nocase;
    this.windowsNoMagicRoot = options.windowsNoMagicRoot !== undefined ? options.windowsNoMagicRoot : !!(this.isWindows && this.nocase);
    this.globSet = [];
    this.globParts = [];
    this.set = [];
    // make the set of regexps etc.
    this.make();
  }
  hasMagic() {
    if (this.options.magicalBraces && this.set.length > 1) {
      return true;
    }
    for (const pattern of this.set) {
      for (const part of pattern) {
        if (typeof part !== 'string') return true;
      }
    }
    return false;
  }
  debug() {}
  make() {
    const pattern = this.pattern;
    const options = this.options;
    // empty patterns and comments match nothing.
    if (!options.nocomment && pattern.charAt(0) === '#') {
      this.comment = true;
      return;
    }
    if (!pattern) {
      this.empty = true;
      return;
    }
    // step 1: figure out negation, etc.
    this.parseNegate();
    // step 2: expand braces
    this.globSet = [...new Set(this.braceExpand())];
    if (options.debug) {
      this.debug = function () {
        return console.error(...arguments);
      };
    }
    this.debug(this.pattern, this.globSet);
    // step 3: now we have a set, so turn each one into a series of
    // path-portion matching patterns.
    // These will be regexps, except in the case of "**", which is
    // set to the GLOBSTAR object for globstar behavior,
    // and will not contain any / characters
    //
    // First, we preprocess to make the glob pattern sets a bit simpler
    // and deduped.  There are some perf-killing patterns that can cause
    // problems with a glob walk, but we can simplify them down a bit.
    const rawGlobParts = this.globSet.map(s => this.slashSplit(s));
    this.globParts = this.preprocess(rawGlobParts);
    this.debug(this.pattern, this.globParts);
    // glob --> regexps
    let set = this.globParts.map((s, _, __) => {
      if (this.isWindows && this.windowsNoMagicRoot) {
        // check if it's a drive or unc path.
        const isUNC = s[0] === '' && s[1] === '' && (s[2] === '?' || !globMagic.test(s[2])) && !globMagic.test(s[3]);
        const isDrive = /^[a-z]:/i.test(s[0]);
        if (isUNC) {
          return [...s.slice(0, 4), ...s.slice(4).map(ss => this.parse(ss))];
        } else if (isDrive) {
          return [s[0], ...s.slice(1).map(ss => this.parse(ss))];
        }
      }
      return s.map(ss => this.parse(ss));
    });
    this.debug(this.pattern, set);
    // filter out everything that didn't compile properly.
    this.set = set.filter(s => s.indexOf(false) === -1);
    // do not treat the ? in UNC paths as magic
    if (this.isWindows) {
      for (let i = 0; i < this.set.length; i++) {
        const p = this.set[i];
        if (p[0] === '' && p[1] === '' && this.globParts[i][2] === '?' && typeof p[3] === 'string' && /^[a-z]:$/i.test(p[3])) {
          p[2] = '?';
        }
      }
    }
    this.debug(this.pattern, this.set);
  }
  // various transforms to equivalent pattern sets that are
  // faster to process in a filesystem walk.  The goal is to
  // eliminate what we can, and push all ** patterns as far
  // to the right as possible, even if it increases the number
  // of patterns that we have to process.
  preprocess(globParts) {
    // if we're not in globstar mode, then turn all ** into *
    if (this.options.noglobstar) {
      for (let i = 0; i < globParts.length; i++) {
        for (let j = 0; j < globParts[i].length; j++) {
          if (globParts[i][j] === '**') {
            globParts[i][j] = '*';
          }
        }
      }
    }
    const {
      optimizationLevel = 1
    } = this.options;
    if (optimizationLevel >= 2) {
      // aggressive optimization for the purpose of fs walking
      globParts = this.firstPhasePreProcess(globParts);
      globParts = this.secondPhasePreProcess(globParts);
    } else if (optimizationLevel >= 1) {
      // just basic optimizations to remove some .. parts
      globParts = this.levelOneOptimize(globParts);
    } else {
      // just collapse multiple ** portions into one
      globParts = this.adjascentGlobstarOptimize(globParts);
    }
    return globParts;
  }
  // just get rid of adjascent ** portions
  adjascentGlobstarOptimize(globParts) {
    return globParts.map(parts => {
      let gs = -1;
      while (-1 !== (gs = parts.indexOf('**', gs + 1))) {
        let i = gs;
        while (parts[i + 1] === '**') {
          i++;
        }
        if (i !== gs) {
          parts.splice(gs, i - gs);
        }
      }
      return parts;
    });
  }
  // get rid of adjascent ** and resolve .. portions
  levelOneOptimize(globParts) {
    return globParts.map(parts => {
      parts = parts.reduce((set, part) => {
        const prev = set[set.length - 1];
        if (part === '**' && prev === '**') {
          return set;
        }
        if (part === '..') {
          if (prev && prev !== '..' && prev !== '.' && prev !== '**') {
            set.pop();
            return set;
          }
        }
        set.push(part);
        return set;
      }, []);
      return parts.length === 0 ? [''] : parts;
    });
  }
  levelTwoFileOptimize(parts) {
    if (!Array.isArray(parts)) {
      parts = this.slashSplit(parts);
    }
    let didSomething = false;
    do {
      didSomething = false;
      // <pre>/<e>/<rest> -> <pre>/<rest>
      if (!this.preserveMultipleSlashes) {
        for (let i = 1; i < parts.length - 1; i++) {
          const p = parts[i];
          // don't squeeze out UNC patterns
          if (i === 1 && p === '' && parts[0] === '') continue;
          if (p === '.' || p === '') {
            didSomething = true;
            parts.splice(i, 1);
            i--;
          }
        }
        if (parts[0] === '.' && parts.length === 2 && (parts[1] === '.' || parts[1] === '')) {
          didSomething = true;
          parts.pop();
        }
      }
      // <pre>/<p>/../<rest> -> <pre>/<rest>
      let dd = 0;
      while (-1 !== (dd = parts.indexOf('..', dd + 1))) {
        const p = parts[dd - 1];
        if (p && p !== '.' && p !== '..' && p !== '**') {
          didSomething = true;
          parts.splice(dd - 1, 2);
          dd -= 2;
        }
      }
    } while (didSomething);
    return parts.length === 0 ? [''] : parts;
  }
  // First phase: single-pattern processing
  // <pre> is 1 or more portions
  // <rest> is 1 or more portions
  // <p> is any portion other than ., .., '', or **
  // <e> is . or ''
  //
  // **/.. is *brutal* for filesystem walking performance, because
  // it effectively resets the recursive walk each time it occurs,
  // and ** cannot be reduced out by a .. pattern part like a regexp
  // or most strings (other than .., ., and '') can be.
  //
  // <pre>/**/../<p>/<p>/<rest> -> {<pre>/../<p>/<p>/<rest>,<pre>/**/<p>/<p>/<rest>}
  // <pre>/<e>/<rest> -> <pre>/<rest>
  // <pre>/<p>/../<rest> -> <pre>/<rest>
  // **/**/<rest> -> **/<rest>
  //
  // **/*/<rest> -> */**/<rest> <== not valid because ** doesn't follow
  // this WOULD be allowed if ** did follow symlinks, or * didn't
  firstPhasePreProcess(globParts) {
    let didSomething = false;
    do {
      didSomething = false;
      // <pre>/**/../<p>/<p>/<rest> -> {<pre>/../<p>/<p>/<rest>,<pre>/**/<p>/<p>/<rest>}
      for (let parts of globParts) {
        let gs = -1;
        while (-1 !== (gs = parts.indexOf('**', gs + 1))) {
          let gss = gs;
          while (parts[gss + 1] === '**') {
            // <pre>/**/**/<rest> -> <pre>/**/<rest>
            gss++;
          }
          // eg, if gs is 2 and gss is 4, that means we have 3 **
          // parts, and can remove 2 of them.
          if (gss > gs) {
            parts.splice(gs + 1, gss - gs);
          }
          let next = parts[gs + 1];
          const p = parts[gs + 2];
          const p2 = parts[gs + 3];
          if (next !== '..') continue;
          if (!p || p === '.' || p === '..' || !p2 || p2 === '.' || p2 === '..') {
            continue;
          }
          didSomething = true;
          // edit parts in place, and push the new one
          parts.splice(gs, 1);
          const other = parts.slice(0);
          other[gs] = '**';
          globParts.push(other);
          gs--;
        }
        // <pre>/<e>/<rest> -> <pre>/<rest>
        if (!this.preserveMultipleSlashes) {
          for (let i = 1; i < parts.length - 1; i++) {
            const p = parts[i];
            // don't squeeze out UNC patterns
            if (i === 1 && p === '' && parts[0] === '') continue;
            if (p === '.' || p === '') {
              didSomething = true;
              parts.splice(i, 1);
              i--;
            }
          }
          if (parts[0] === '.' && parts.length === 2 && (parts[1] === '.' || parts[1] === '')) {
            didSomething = true;
            parts.pop();
          }
        }
        // <pre>/<p>/../<rest> -> <pre>/<rest>
        let dd = 0;
        while (-1 !== (dd = parts.indexOf('..', dd + 1))) {
          const p = parts[dd - 1];
          if (p && p !== '.' && p !== '..' && p !== '**') {
            didSomething = true;
            const needDot = dd === 1 && parts[dd + 1] === '**';
            const splin = needDot ? ['.'] : [];
            parts.splice(dd - 1, 2, ...splin);
            if (parts.length === 0) parts.push('');
            dd -= 2;
          }
        }
      }
    } while (didSomething);
    return globParts;
  }
  // second phase: multi-pattern dedupes
  // {<pre>/*/<rest>,<pre>/<p>/<rest>} -> <pre>/*/<rest>
  // {<pre>/<rest>,<pre>/<rest>} -> <pre>/<rest>
  // {<pre>/**/<rest>,<pre>/<rest>} -> <pre>/**/<rest>
  //
  // {<pre>/**/<rest>,<pre>/**/<p>/<rest>} -> <pre>/**/<rest>
  // ^-- not valid because ** doens't follow symlinks
  secondPhasePreProcess(globParts) {
    for (let i = 0; i < globParts.length - 1; i++) {
      for (let j = i + 1; j < globParts.length; j++) {
        const matched = this.partsMatch(globParts[i], globParts[j], !this.preserveMultipleSlashes);
        if (matched) {
          globParts[i] = [];
          globParts[j] = matched;
          break;
        }
      }
    }
    return globParts.filter(gs => gs.length);
  }
  partsMatch(a, b) {
    let emptyGSMatch = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : false;
    let ai = 0;
    let bi = 0;
    let result = [];
    let which = '';
    while (ai < a.length && bi < b.length) {
      if (a[ai] === b[bi]) {
        result.push(which === 'b' ? b[bi] : a[ai]);
        ai++;
        bi++;
      } else if (emptyGSMatch && a[ai] === '**' && b[bi] === a[ai + 1]) {
        result.push(a[ai]);
        ai++;
      } else if (emptyGSMatch && b[bi] === '**' && a[ai] === b[bi + 1]) {
        result.push(b[bi]);
        bi++;
      } else if (a[ai] === '*' && b[bi] && (this.options.dot || !b[bi].startsWith('.')) && b[bi] !== '**') {
        if (which === 'b') return false;
        which = 'a';
        result.push(a[ai]);
        ai++;
        bi++;
      } else if (b[bi] === '*' && a[ai] && (this.options.dot || !a[ai].startsWith('.')) && a[ai] !== '**') {
        if (which === 'a') return false;
        which = 'b';
        result.push(b[bi]);
        ai++;
        bi++;
      } else {
        return false;
      }
    }
    // if we fall out of the loop, it means they two are identical
    // as long as their lengths match
    return a.length === b.length && result;
  }
  parseNegate() {
    if (this.nonegate) return;
    const pattern = this.pattern;
    let negate = false;
    let negateOffset = 0;
    for (let i = 0; i < pattern.length && pattern.charAt(i) === '!'; i++) {
      negate = !negate;
      negateOffset++;
    }
    if (negateOffset) this.pattern = pattern.slice(negateOffset);
    this.negate = negate;
  }
  // set partial to true to test if, for example,
  // "/a/b" matches the start of "/*/b/*/d"
  // Partial means, if you run out of file before you run
  // out of pattern, then that's fine, as long as all
  // the parts match.
  matchOne(file, pattern) {
    let partial = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : false;
    const options = this.options;
    // UNC paths like //?/X:/... can match X:/... and vice versa
    // Drive letters in absolute drive or unc paths are always compared
    // case-insensitively.
    if (this.isWindows) {
      const fileDrive = typeof file[0] === 'string' && /^[a-z]:$/i.test(file[0]);
      const fileUNC = !fileDrive && file[0] === '' && file[1] === '' && file[2] === '?' && /^[a-z]:$/i.test(file[3]);
      const patternDrive = typeof pattern[0] === 'string' && /^[a-z]:$/i.test(pattern[0]);
      const patternUNC = !patternDrive && pattern[0] === '' && pattern[1] === '' && pattern[2] === '?' && typeof pattern[3] === 'string' && /^[a-z]:$/i.test(pattern[3]);
      const fdi = fileUNC ? 3 : fileDrive ? 0 : undefined;
      const pdi = patternUNC ? 3 : patternDrive ? 0 : undefined;
      if (typeof fdi === 'number' && typeof pdi === 'number') {
        const [fd, pd] = [file[fdi], pattern[pdi]];
        if (fd.toLowerCase() === pd.toLowerCase()) {
          pattern[pdi] = fd;
          if (pdi > fdi) {
            pattern = pattern.slice(pdi);
          } else if (fdi > pdi) {
            file = file.slice(fdi);
          }
        }
      }
    }
    // resolve and reduce . and .. portions in the file as well.
    // dont' need to do the second phase, because it's only one string[]
    const {
      optimizationLevel = 1
    } = this.options;
    if (optimizationLevel >= 2) {
      file = this.levelTwoFileOptimize(file);
    }
    this.debug('matchOne', this, {
      file,
      pattern
    });
    this.debug('matchOne', file.length, pattern.length);
    for (var fi = 0, pi = 0, fl = file.length, pl = pattern.length; fi < fl && pi < pl; fi++, pi++) {
      this.debug('matchOne loop');
      var p = pattern[pi];
      var f = file[fi];
      this.debug(pattern, p, f);
      // should be impossible.
      // some invalid regexp stuff in the set.
      /* c8 ignore start */
      if (p === false) {
        return false;
      }
      /* c8 ignore stop */
      if (p === GLOBSTAR) {
        this.debug('GLOBSTAR', [pattern, p, f]);
        // "**"
        // a/**/b/**/c would match the following:
        // a/b/x/y/z/c
        // a/x/y/z/b/c
        // a/b/x/b/x/c
        // a/b/c
        // To do this, take the rest of the pattern after
        // the **, and see if it would match the file remainder.
        // If so, return success.
        // If not, the ** "swallows" a segment, and try again.
        // This is recursively awful.
        //
        // a/**/b/**/c matching a/b/x/y/z/c
        // - a matches a
        // - doublestar
        //   - matchOne(b/x/y/z/c, b/**/c)
        //     - b matches b
        //     - doublestar
        //       - matchOne(x/y/z/c, c) -> no
        //       - matchOne(y/z/c, c) -> no
        //       - matchOne(z/c, c) -> no
        //       - matchOne(c, c) yes, hit
        var fr = fi;
        var pr = pi + 1;
        if (pr === pl) {
          this.debug('** at the end');
          // a ** at the end will just swallow the rest.
          // We have found a match.
          // however, it will not swallow /.x, unless
          // options.dot is set.
          // . and .. are *never* matched by **, for explosively
          // exponential reasons.
          for (; fi < fl; fi++) {
            if (file[fi] === '.' || file[fi] === '..' || !options.dot && file[fi].charAt(0) === '.') return false;
          }
          return true;
        }
        // ok, let's see if we can swallow whatever we can.
        while (fr < fl) {
          var swallowee = file[fr];
          this.debug('\nglobstar while', file, fr, pattern, pr, swallowee);
          // XXX remove this slice.  Just pass the start index.
          if (this.matchOne(file.slice(fr), pattern.slice(pr), partial)) {
            this.debug('globstar found match!', fr, fl, swallowee);
            // found a match.
            return true;
          } else {
            // can't swallow "." or ".." ever.
            // can only swallow ".foo" when explicitly asked.
            if (swallowee === '.' || swallowee === '..' || !options.dot && swallowee.charAt(0) === '.') {
              this.debug('dot detected!', file, fr, pattern, pr);
              break;
            }
            // ** swallows a segment, and continue.
            this.debug('globstar swallow a segment, and continue');
            fr++;
          }
        }
        // no match was found.
        // However, in partial mode, we can't say this is necessarily over.
        /* c8 ignore start */
        if (partial) {
          // ran out of file
          this.debug('\n>>> no match, partial?', file, fr, pattern, pr);
          if (fr === fl) {
            return true;
          }
        }
        /* c8 ignore stop */
        return false;
      }
      // something other than **
      // non-magic patterns just have to match exactly
      // patterns with magic have been turned into regexps.
      let hit;
      if (typeof p === 'string') {
        hit = f === p;
        this.debug('string match', p, f, hit);
      } else {
        hit = p.test(f);
        this.debug('pattern match', p, f, hit);
      }
      if (!hit) return false;
    }
    // Note: ending in / means that we'll get a final ""
    // at the end of the pattern.  This can only match a
    // corresponding "" at the end of the file.
    // If the file ends in /, then it can only match a
    // a pattern that ends in /, unless the pattern just
    // doesn't have any more for it. But, a/b/ should *not*
    // match "a/b/*", even though "" matches against the
    // [^/]*? pattern, except in partial mode, where it might
    // simply not be reached yet.
    // However, a/b/ should still satisfy a/*
    // now either we fell off the end of the pattern, or we're done.
    if (fi === fl && pi === pl) {
      // ran out of pattern and filename at the same time.
      // an exact hit!
      return true;
    } else if (fi === fl) {
      // ran out of file, but still had pattern left.
      // this is ok if we're doing the match as part of
      // a glob fs traversal.
      return partial;
    } else if (pi === pl) {
      // ran out of pattern, still have file left.
      // this is only acceptable if we're on the very last
      // empty segment of a file with a trailing slash.
      // a/* should match a/b/
      return fi === fl - 1 && file[fi] === '';
      /* c8 ignore start */
    } else {
      // should be unreachable.
      throw new Error('wtf?');
    }
    /* c8 ignore stop */
  }
  braceExpand() {
    return braceExpand(this.pattern, this.options);
  }
  parse(pattern) {
    assertValidPattern(pattern);
    const options = this.options;
    // shortcuts
    if (pattern === '**') return GLOBSTAR;
    if (pattern === '') return '';
    // far and away, the most common glob pattern parts are
    // *, *.*, and *.<ext>  Add a fast check method for those.
    let m;
    let fastTest = null;
    if (m = pattern.match(starRE)) {
      fastTest = options.dot ? starTestDot : starTest;
    } else if (m = pattern.match(starDotExtRE)) {
      fastTest = (options.nocase ? options.dot ? starDotExtTestNocaseDot : starDotExtTestNocase : options.dot ? starDotExtTestDot : starDotExtTest)(m[1]);
    } else if (m = pattern.match(qmarksRE)) {
      fastTest = (options.nocase ? options.dot ? qmarksTestNocaseDot : qmarksTestNocase : options.dot ? qmarksTestDot : qmarksTest)(m);
    } else if (m = pattern.match(starDotStarRE)) {
      fastTest = options.dot ? starDotStarTestDot : starDotStarTest;
    } else if (m = pattern.match(dotStarRE)) {
      fastTest = dotStarTest;
    }
    const re = AST.fromGlob(pattern, this.options).toMMPattern();
    if (fastTest && typeof re === 'object') {
      // Avoids overriding in frozen environments
      Reflect.defineProperty(re, 'test', {
        value: fastTest
      });
    }
    return re;
  }
  makeRe() {
    if (this.regexp || this.regexp === false) return this.regexp;
    // at this point, this.set is a 2d array of partial
    // pattern strings, or "**".
    //
    // It's better to use .match().  This function shouldn't
    // be used, really, but it's pretty convenient sometimes,
    // when you just want to work with a regex.
    const set = this.set;
    if (!set.length) {
      this.regexp = false;
      return this.regexp;
    }
    const options = this.options;
    const twoStar = options.noglobstar ? esm_star : options.dot ? twoStarDot : twoStarNoDot;
    const flags = new Set(options.nocase ? ['i'] : []);
    // regexpify non-globstar patterns
    // if ** is only item, then we just do one twoStar
    // if ** is first, and there are more, prepend (\/|twoStar\/)? to next
    // if ** is last, append (\/twoStar|) to previous
    // if ** is in the middle, append (\/|\/twoStar\/) to previous
    // then filter out GLOBSTAR symbols
    let re = set.map(pattern => {
      const pp = pattern.map(p => {
        if (p instanceof RegExp) {
          for (const f of p.flags.split('')) flags.add(f);
        }
        return typeof p === 'string' ? esm_regExpEscape(p) : p === GLOBSTAR ? GLOBSTAR : p._src;
      });
      pp.forEach((p, i) => {
        const next = pp[i + 1];
        const prev = pp[i - 1];
        if (p !== GLOBSTAR || prev === GLOBSTAR) {
          return;
        }
        if (prev === undefined) {
          if (next !== undefined && next !== GLOBSTAR) {
            pp[i + 1] = '(?:\\/|' + twoStar + '\\/)?' + next;
          } else {
            pp[i] = twoStar;
          }
        } else if (next === undefined) {
          pp[i - 1] = prev + '(?:\\/|' + twoStar + ')?';
        } else if (next !== GLOBSTAR) {
          pp[i - 1] = prev + '(?:\\/|\\/' + twoStar + '\\/)' + next;
          pp[i + 1] = GLOBSTAR;
        }
      });
      return pp.filter(p => p !== GLOBSTAR).join('/');
    }).join('|');
    // need to wrap in parens if we had more than one thing with |,
    // otherwise only the first will be anchored to ^ and the last to $
    const [open, close] = set.length > 1 ? ['(?:', ')'] : ['', ''];
    // must match entire pattern
    // ending in a * or ** will make it less strict.
    re = '^' + open + re + close + '$';
    // can match anything, as long as it's not this.
    if (this.negate) re = '^(?!' + re + ').+$';
    try {
      this.regexp = new RegExp(re, [...flags].join(''));
      /* c8 ignore start */
    } catch (ex) {
      // should be impossible
      this.regexp = false;
    }
    /* c8 ignore stop */
    return this.regexp;
  }
  slashSplit(p) {
    // if p starts with // on windows, we preserve that
    // so that UNC paths aren't broken.  Otherwise, any number of
    // / characters are coalesced into one, unless
    // preserveMultipleSlashes is set to true.
    if (this.preserveMultipleSlashes) {
      return p.split('/');
    } else if (this.isWindows && /^\/\/[^\/]+/.test(p)) {
      // add an extra '' for the one we lose
      return ['', ...p.split(/\/+/)];
    } else {
      return p.split(/\/+/);
    }
  }
  match(f) {
    let partial = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : this.partial;
    this.debug('match', f, this.pattern);
    // short-circuit in the case of busted things.
    // comments, etc.
    if (this.comment) {
      return false;
    }
    if (this.empty) {
      return f === '';
    }
    if (f === '/' && partial) {
      return true;
    }
    const options = this.options;
    // windows: need to use /, not \
    if (this.isWindows) {
      f = f.split('\\').join('/');
    }
    // treat the test path as a set of pathparts.
    const ff = this.slashSplit(f);
    this.debug(this.pattern, 'split', ff);
    // just ONE of the pattern sets in this.set needs to match
    // in order for it to be valid.  If negating, then just one
    // match means that we have failed.
    // Either way, return on the first hit.
    const set = this.set;
    this.debug(this.pattern, 'set', set);
    // Find the basename of the path by looking for the last non-empty segment
    let filename = ff[ff.length - 1];
    if (!filename) {
      for (let i = ff.length - 2; !filename && i >= 0; i--) {
        filename = ff[i];
      }
    }
    for (let i = 0; i < set.length; i++) {
      const pattern = set[i];
      let file = ff;
      if (options.matchBase && pattern.length === 1) {
        file = [filename];
      }
      const hit = this.matchOne(file, pattern, partial);
      if (hit) {
        if (options.flipNegate) {
          return true;
        }
        return !this.negate;
      }
    }
    // didn't get any hits.  this is success if it's a negative
    // pattern, failure otherwise.
    if (options.flipNegate) {
      return false;
    }
    return this.negate;
  }
  static defaults(def) {
    return minimatch.defaults(def).Minimatch;
  }
}
/* c8 ignore start */



/* c8 ignore stop */
minimatch.AST = AST;
minimatch.Minimatch = Minimatch;
minimatch.escape = escape_escape;
minimatch.unescape = unescape_unescape;
;// CONCATENATED MODULE: ./source/response.ts


function createErrorFromResponse(response) {
  let prefix = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : "";
  const err = new Error(`${prefix}Invalid response: ${response.status} ${response.statusText}`);
  err.status = response.status;
  err.response = response;
  return err;
}
function handleResponseCode(context, response) {
  const {
    status
  } = response;
  if (status === 401 && context.digest) return response;
  if (status >= 400) {
    const err = createErrorFromResponse(response);
    throw err;
  }
  return response;
}
function processGlobFilter(files, glob) {
  return files.filter(file => minimatch(file.filename, glob, {
    matchBase: true
  }));
}

/**
 * Process a response payload (eg. from `customRequest`) and
 *  prepare it for further processing. Exposed for custom
 *  request handling.
 * @param response The response for a request
 * @param data The data returned
 * @param isDetailed Whether or not a detailed result is
 *  requested
 * @returns The response data, or a detailed response object
 *  if required
 */
function processResponsePayload(response, data) {
  let isDetailed = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : false;
  return isDetailed ? {
    data,
    headers: response.headers ? convertResponseHeaders(response.headers) : {},
    status: response.status,
    statusText: response.statusText
  } : data;
}
;// CONCATENATED MODULE: ./source/operations/copyFile.ts




function copyFile_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function copyFile_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const copyFile = copyFile_async(function (context, filename, destination) {
  let options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filename)),
    method: "COPY",
    headers: {
      Destination: joinURL(context.remoteURL, encodePath(destination)),
      /**
       * From RFC4918 section 10.6: If the overwrite header is not included in a COPY or MOVE request,
       * then the resource MUST treat the request as if it has an overwrite header of value "T".
       *
       * Meaning the overwrite header is always set to "T" EXCEPT the option is explicitly set to false.
       */
      Overwrite: options.overwrite === false ? "F" : "T",
      /**
       * From RFC4918 section 9.8.3: A client may submit a Depth header on a COPY on a collection with a value of "0"
       * or "infinity". The COPY method on a collection without a Depth header MUST act as if
       * a Depth header with value "infinity" was included.
       */
      Depth: options.shallow ? "0" : "infinity"
    }
  }, context, options);
  return copyFile_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
  });
});
// EXTERNAL MODULE: ./node_modules/fast-xml-parser/src/fxp.js
var fxp = __webpack_require__(635);
// EXTERNAL MODULE: ./node_modules/nested-property/dist/nested-property.js
var nested_property = __webpack_require__(829);
var nested_property_default = /*#__PURE__*/__webpack_require__.n(nested_property);
;// CONCATENATED MODULE: ./source/tools/dav.ts




var PropertyType = /*#__PURE__*/function (PropertyType) {
  PropertyType["Array"] = "array";
  PropertyType["Object"] = "object";
  PropertyType["Original"] = "original";
  return PropertyType;
}(PropertyType || {});
function getParser() {
  return new fxp.XMLParser({
    removeNSPrefix: true,
    numberParseOptions: {
      hex: true,
      leadingZeros: false
    },
    tagValueProcessor(tagName, tagValue, jPath) {
      if (jPath.endsWith("propstat.prop.displayname")) {
        // Do not parse the display name, because this causes e.g. '2024.10' to result in number 2024.1
        return;
      }
      return tagValue;
    }
    // We don't use the processors here as decoding is done manually
    // later on - decoding early would break some path checks.
  });
}
function getPropertyOfType(obj, prop) {
  let type = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : PropertyType.Original;
  const val = nested_property_default().get(obj, prop);
  if (type === "array" && Array.isArray(val) === false) {
    return [val];
  } else if (type === "object" && Array.isArray(val)) {
    return val[0];
  }
  return val;
}
function normaliseResponse(response) {
  const output = Object.assign({}, response);
  // Only either status OR propstat is allowed
  if (output.status) {
    nested_property_default().set(output, "status", getPropertyOfType(output, "status", PropertyType.Object));
  } else {
    nested_property_default().set(output, "propstat", getPropertyOfType(output, "propstat", PropertyType.Object));
    nested_property_default().set(output, "propstat.prop", getPropertyOfType(output, "propstat.prop", PropertyType.Object));
  }
  return output;
}
function normaliseResult(result) {
  const {
    multistatus
  } = result;
  if (multistatus === "") {
    return {
      multistatus: {
        response: []
      }
    };
  }
  if (!multistatus) {
    throw new Error("Invalid response: No root multistatus found");
  }
  const output = {
    multistatus: Array.isArray(multistatus) ? multistatus[0] : multistatus
  };
  nested_property_default().set(output, "multistatus.response", getPropertyOfType(output, "multistatus.response", PropertyType.Array));
  nested_property_default().set(output, "multistatus.response", nested_property_default().get(output, "multistatus.response").map(response => normaliseResponse(response)));
  return output;
}

/**
 * Parse an XML response from a WebDAV service,
 *  converting it to an internal DAV result
 * @param xml The raw XML string
 * @returns A parsed and processed DAV result
 */
function parseXML(xml) {
  return new Promise(resolve => {
    const result = getParser().parse(xml);
    resolve(normaliseResult(result));
  });
}

/**
 * Get a file stat result from given DAV properties
 * @param props DAV properties
 * @param filename The filename for the file stat
 * @param isDetailed Whether or not the raw props of the resource should be returned
 * @returns A file stat result
 */
function prepareFileFromProps(props, filename) {
  let isDetailed = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : false;
  // Last modified time, raw size, item type and mime
  const {
    getlastmodified: lastMod = null,
    getcontentlength: rawSize = "0",
    resourcetype: resourceType = null,
    getcontenttype: mimeType = null,
    getetag: etag = null
  } = props;
  const type = resourceType && typeof resourceType === "object" && typeof resourceType.collection !== "undefined" ? "directory" : "file";
  const stat = {
    filename,
    basename: path_posix_default().basename(filename),
    lastmod: lastMod,
    size: parseInt(rawSize, 10),
    type,
    etag: typeof etag === "string" ? etag.replace(/"/g, "") : null
  };
  if (type === "file") {
    stat.mime = mimeType && typeof mimeType === "string" ? mimeType.split(";")[0] : "";
  }
  if (isDetailed) {
    // The XML parser tries to interpret values, but the display name is required to be a string
    if (typeof props.displayname !== "undefined") {
      props.displayname = String(props.displayname);
    }
    stat.props = props;
  }
  return stat;
}

/**
 * Parse a DAV result for file stats
 * @param result The resulting DAV response
 * @param filename The filename that was stat'd
 * @param isDetailed Whether or not the raw props of
 *  the resource should be returned
 * @returns A file stat result
 */
function parseStat(result, filename) {
  let isDetailed = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : false;
  let responseItem = null;
  try {
    // should be a propstat response, if not the if below will throw an error
    if (result.multistatus.response[0].propstat) {
      responseItem = result.multistatus.response[0];
    }
  } catch (e) {
    /* ignore */
  }
  if (!responseItem) {
    throw new Error("Failed getting item stat: bad response");
  }
  const {
    propstat: {
      prop: props,
      status: statusLine
    }
  } = responseItem;

  // As defined in https://tools.ietf.org/html/rfc2068#section-6.1
  const [_, statusCodeStr, statusText] = statusLine.split(" ", 3);
  const statusCode = parseInt(statusCodeStr, 10);
  if (statusCode >= 400) {
    const err = new Error(`Invalid response: ${statusCode} ${statusText}`);
    err.status = statusCode;
    throw err;
  }
  const filePath = normalisePath(filename);
  return prepareFileFromProps(props, filePath, isDetailed);
}

/**
 * Parse a DAV result for a search request
 *
 * @param result The resulting DAV response
 * @param searchArbiter The collection path that was searched
 * @param isDetailed Whether or not the raw props of the resource should be returned
 */
function parseSearch(result, searchArbiter, isDetailed) {
  const response = {
    truncated: false,
    results: []
  };
  response.truncated = result.multistatus.response.some(v => {
    return (v.status || v.propstat?.status).split(" ", 3)?.[1] === "507" && v.href.replace(/\/$/, "").endsWith(encodePath(searchArbiter).replace(/\/$/, ""));
  });
  result.multistatus.response.forEach(result => {
    if (result.propstat === undefined) {
      return;
    }
    const filename = result.href.split("/").map(decodeURIComponent).join("/");
    response.results.push(prepareFileFromProps(result.propstat.prop, filename, isDetailed));
  });
  return response;
}

/**
 * Translate a disk quota indicator to a recognised
 *  value (includes "unlimited" and "unknown")
 * @param value The quota indicator, eg. "-3"
 * @returns The value in bytes, or another indicator
 */
function translateDiskSpace(value) {
  switch (String(value)) {
    case "-3":
      return "unlimited";
    case "-2":
    /* falls-through */
    case "-1":
      // -1 is non-computed
      return "unknown";
    default:
      return parseInt(String(value), 10);
  }
}
;// CONCATENATED MODULE: ./source/operations/stat.ts





function stat_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function stat_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const getStat = stat_async(function (context, filename) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const {
    details: isDetailed = false
  } = options;
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filename)),
    method: "PROPFIND",
    headers: {
      Accept: "text/plain,application/xml",
      Depth: "0"
    }
  }, context, options);
  return stat_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
    return stat_await(response.text(), function (responseData) {
      return stat_await(parseXML(responseData), function (result) {
        const stat = parseStat(result, filename, isDetailed);
        return processResponsePayload(response, stat, isDetailed);
      });
    });
  });
});
;// CONCATENATED MODULE: ./source/operations/createDirectory.ts





function createDirectory_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
const createDirectoryRecursively = createDirectory_async(function (context, dirPath) {
  let _exit = false;
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const paths = getAllDirectories(normalisePath(dirPath));
  paths.sort((a, b) => {
    if (a.length > b.length) {
      return 1;
    } else if (b.length > a.length) {
      return -1;
    }
    return 0;
  });
  let creating = false;
  return _forOf(paths, function (testPath) {
    return createDirectory_invoke(function () {
      if (creating) {
        return _awaitIgnored(createDirectory(context, testPath, {
          ...options,
          recursive: false
        }));
      }
    }, function () {
      return _catch(function () {
        return createDirectory_await(getStat(context, testPath), function (_getStat) {
          const testStat = _getStat;
          if (testStat.type !== "directory") {
            throw new Error(`Path includes a file: ${dirPath}`);
          }
        });
      }, function (err) {
        const error = err;
        return function () {
          if (error.status === 404) {
            creating = true;
            return _awaitIgnored(createDirectory(context, testPath, {
              ...options,
              recursive: false
            }));
          } else {
            throw err;
          }
        }();
      });
    });
  }, function () {
    return _exit;
  });
});
function createDirectory_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
} /**
   * Ensure the path is a proper "collection" path by ensuring it has a trailing "/".
   * The proper format of collection according to the specification does contain the trailing slash.
   * http://www.webdav.org/specs/rfc4918.html#rfc.section.5.2
   * @param path Path of the collection
   * @return string Path of the collection with appended trailing "/" in case the `path` does not have it.
   */

function _empty() {}
function _awaitIgnored(value, direct) {
  if (!direct) {
    return value && value.then ? value.then(_empty) : Promise.resolve();
  }
}
function _catch(body, recover) {
  try {
    var result = body();
  } catch (e) {
    return recover(e);
  }
  if (result && result.then) {
    return result.then(void 0, recover);
  }
  return result;
}
function createDirectory_invoke(body, then) {
  var result = body();
  if (result && result.then) {
    return result.then(then);
  }
  return then(result);
}
const _iteratorSymbol = /*#__PURE__*/typeof Symbol !== "undefined" ? Symbol.iterator || (Symbol.iterator = Symbol("Symbol.iterator")) : "@@iterator";
function _settle(pact, state, value) {
  if (!pact.s) {
    if (value instanceof _Pact) {
      if (value.s) {
        if (state & 1) {
          state = value.s;
        }
        value = value.v;
      } else {
        value.o = _settle.bind(null, pact, state);
        return;
      }
    }
    if (value && value.then) {
      value.then(_settle.bind(null, pact, state), _settle.bind(null, pact, 2));
      return;
    }
    pact.s = state;
    pact.v = value;
    const observer = pact.o;
    if (observer) {
      observer(pact);
    }
  }
}
const _Pact = /*#__PURE__*/function () {
  function _Pact() {}
  _Pact.prototype.then = function (onFulfilled, onRejected) {
    const result = new _Pact();
    const state = this.s;
    if (state) {
      const callback = state & 1 ? onFulfilled : onRejected;
      if (callback) {
        try {
          _settle(result, 1, callback(this.v));
        } catch (e) {
          _settle(result, 2, e);
        }
        return result;
      } else {
        return this;
      }
    }
    this.o = function (_this) {
      try {
        const value = _this.v;
        if (_this.s & 1) {
          _settle(result, 1, onFulfilled ? onFulfilled(value) : value);
        } else if (onRejected) {
          _settle(result, 1, onRejected(value));
        } else {
          _settle(result, 2, value);
        }
      } catch (e) {
        _settle(result, 2, e);
      }
    };
    return result;
  };
  return _Pact;
}();
function _isSettledPact(thenable) {
  return thenable instanceof _Pact && thenable.s & 1;
}
function _forTo(array, body, check) {
  var i = -1,
    pact,
    reject;
  function _cycle(result) {
    try {
      while (++i < array.length && (!check || !check())) {
        result = body(i);
        if (result && result.then) {
          if (_isSettledPact(result)) {
            result = result.v;
          } else {
            result.then(_cycle, reject || (reject = _settle.bind(null, pact = new _Pact(), 2)));
            return;
          }
        }
      }
      if (pact) {
        _settle(pact, 1, result);
      } else {
        pact = result;
      }
    } catch (e) {
      _settle(pact || (pact = new _Pact()), 2, e);
    }
  }
  _cycle();
  return pact;
}
function _forOf(target, body, check) {
  if (typeof target[_iteratorSymbol] === "function") {
    var iterator = target[_iteratorSymbol](),
      step,
      pact,
      reject;
    function _cycle(result) {
      try {
        while (!(step = iterator.next()).done && (!check || !check())) {
          result = body(step.value);
          if (result && result.then) {
            if (_isSettledPact(result)) {
              result = result.v;
            } else {
              result.then(_cycle, reject || (reject = _settle.bind(null, pact = new _Pact(), 2)));
              return;
            }
          }
        }
        if (pact) {
          _settle(pact, 1, result);
        } else {
          pact = result;
        }
      } catch (e) {
        _settle(pact || (pact = new _Pact()), 2, e);
      }
    }
    _cycle();
    if (iterator.return) {
      var _fixup = function (value) {
        try {
          if (!step.done) {
            iterator.return();
          }
        } catch (e) {}
        return value;
      };
      if (pact && pact.then) {
        return pact.then(_fixup, function (e) {
          throw _fixup(e);
        });
      }
      _fixup();
    }
    return pact;
  }
  // No support for Symbol.iterator
  if (!("length" in target)) {
    throw new TypeError("Object is not iterable");
  }
  // Handle live collections properly
  var values = [];
  for (var i = 0; i < target.length; i++) {
    values.push(target[i]);
  }
  return _forTo(values, function (i) {
    return body(values[i]);
  }, check);
}
const createDirectory = createDirectory_async(function (context, dirPath) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  if (options.recursive === true) return createDirectoryRecursively(context, dirPath, options);
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, ensureCollectionPath(encodePath(dirPath))),
    method: "MKCOL"
  }, context, options);
  return createDirectory_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
  });
});
function ensureCollectionPath(path) {
  if (!path.endsWith("/")) {
    return path + "/";
  }
  return path;
}
// EXTERNAL MODULE: stream (ignored)
var stream_ignored_0 = __webpack_require__(388);
var stream_ignored_default_0 = /*#__PURE__*/__webpack_require__.n(stream_ignored_0);
;// CONCATENATED MODULE: ./source/operations/createStream.ts





function createStream_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
const getFileStream = createStream_async(function (context, filePath) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const headers = {};
  if (typeof options.range === "object" && typeof options.range.start === "number") {
    let rangeHeader = `bytes=${options.range.start}-`;
    if (typeof options.range.end === "number") {
      rangeHeader = `${rangeHeader}${options.range.end}`;
    }
    headers.Range = rangeHeader;
  }
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filePath)),
    method: "GET",
    headers
  }, context, options);
  return createStream_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
    if (headers.Range && response.status !== 206) {
      const responseError = new Error(`Invalid response code for partial request: ${response.status}`);
      responseError.status = response.status;
      throw responseError;
    }
    if (options.callback) {
      setTimeout(() => {
        options.callback(response);
      }, 0);
    }
    // @ts-ignore
    return response.body;
  });
});
function createStream_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const createStream_NOOP = () => {};
function createReadStream(context, filePath) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const PassThroughStream = (stream_ignored_default_0()).PassThrough;
  const outStream = new PassThroughStream();
  getFileStream(context, filePath, options).then(stream => {
    stream.pipe(outStream);
  }).catch(err => {
    outStream.emit("error", err);
  });
  return outStream;
}
function createWriteStream(context, filePath) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  let callback = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : createStream_NOOP;
  const PassThroughStream = (stream_ignored_default_0()).PassThrough;
  const writeStream = new PassThroughStream();
  const headers = {};
  if (options.overwrite === false) {
    headers["If-None-Match"] = "*";
  }
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filePath)),
    method: "PUT",
    headers,
    data: writeStream,
    maxRedirects: 0
  }, context, options);
  request(requestOptions, context).then(response => handleResponseCode(context, response)).then(response => {
    // Fire callback asynchronously to avoid errors
    setTimeout(() => {
      callback(response);
    }, 0);
  }).catch(err => {
    writeStream.emit("error", err);
  });
  return writeStream;
}
;// CONCATENATED MODULE: ./source/operations/customRequest.ts




function customRequest_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function customRequest_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const customRequest = customRequest_async(function (context, remotePath, requestOptions) {
  if (!requestOptions.url) {
    requestOptions.url = joinURL(context.remoteURL, encodePath(remotePath));
  }
  const finalOptions = prepareRequestOptions(requestOptions, context, {});
  return customRequest_await(request(finalOptions, context), function (response) {
    handleResponseCode(context, response);
    return response;
  });
});
;// CONCATENATED MODULE: ./source/operations/deleteFile.ts




function deleteFile_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function deleteFile_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const deleteFile = deleteFile_async(function (context, filename) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filename)),
    method: "DELETE"
  }, context, options);
  return deleteFile_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
  });
});
;// CONCATENATED MODULE: ./source/operations/exists.ts

function exists_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function exists_catch(body, recover) {
  try {
    var result = body();
  } catch (e) {
    return recover(e);
  }
  if (result && result.then) {
    return result.then(void 0, recover);
  }
  return result;
}
function exists_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const exists = exists_async(function (context, remotePath) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  return exists_catch(function () {
    return exists_await(getStat(context, remotePath, options), function () {
      return true;
    });
  }, function (err) {
    if (err.status === 404) {
      return false;
    }
    throw err;
  });
});
;// CONCATENATED MODULE: ./source/operations/directoryContents.ts






function directoryContents_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function directoryContents_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const getDirectoryContents = directoryContents_async(function (context, remotePath) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(remotePath), "/"),
    method: "PROPFIND",
    headers: {
      Accept: "text/plain,application/xml",
      Depth: options.deep ? "infinity" : "1"
    }
  }, context, options);
  return directoryContents_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
    return directoryContents_await(response.text(), function (responseData) {
      if (!responseData) {
        throw new Error("Failed parsing directory contents: Empty response");
      }
      return directoryContents_await(parseXML(responseData), function (davResp) {
        const _remotePath = makePathAbsolute(remotePath);
        const remoteBasePath = makePathAbsolute(context.remoteBasePath || context.remotePath);
        let files = getDirectoryFiles(davResp, remoteBasePath, _remotePath, options.details, options.includeSelf);
        if (options.glob) {
          files = processGlobFilter(files, options.glob);
        }
        return processResponsePayload(response, files, options.details);
      });
    });
  });
});
function getDirectoryFiles(result, serverremoteBasePath, requestPath) {
  let isDetailed = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : false;
  let includeSelf = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : false;
  const serverBase = path_posix_default().join(serverremoteBasePath, "/");
  // Extract the response items (directory contents)
  const {
    multistatus: {
      response: responseItems
    }
  } = result;

  // Map all items to a consistent output structure (results)
  const nodes = responseItems.map(item => {
    // HREF is the file path (in full) - The href is already XML entities decoded (e.g. foo&amp;bar is reverted to foo&bar)
    const href = normaliseHREF(item.href);
    // Each item should contain a stat object
    const {
      propstat: {
        prop: props
      }
    } = item;
    // Process the true full filename (minus the base server path)
    const filename = serverBase === "/" ? decodeURIComponent(normalisePath(href)) : normalisePath(path_posix_default().relative(decodeURIComponent(serverBase), decodeURIComponent(href)));
    return prepareFileFromProps(props, filename, isDetailed);
  });

  // If specified, also return the current directory
  if (includeSelf) {
    return nodes;
  }

  // Else, filter out the item pointing to the current directory (not needed)
  return nodes.filter(item => item.basename && (item.type === "file" || item.filename !== requestPath.replace(/\/$/, "")));
}
;// CONCATENATED MODULE: ./source/operations/getFileContents.ts








function getFileContents_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const getFileContentsString = getFileContents_async(function (context, filePath) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filePath)),
    method: "GET",
    headers: {
      Accept: "text/plain"
    },
    transformResponse: [TRANSFORM_RETAIN_FORMAT]
  }, context, options);
  return getFileContents_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
    return getFileContents_await(response.text(), function (body) {
      return processResponsePayload(response, body, options.details);
    });
  });
});
function getFileContents_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
const getFileContentsBuffer = getFileContents_async(function (context, filePath) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filePath)),
    method: "GET"
  }, context, options);
  return getFileContents_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
    let body;
    return getFileContents_invoke(function () {
      if (env_isWeb() || isReactNative()) {
        return getFileContents_await(response.arrayBuffer(), function (_response$arrayBuffer) {
          body = _response$arrayBuffer;
        });
      } else {
        const _Buffer = Buffer,
          _from = _Buffer.from;
        return getFileContents_await(response.arrayBuffer(), function (_response$arrayBuffer2) {
          body = _from.call(_Buffer, _response$arrayBuffer2);
        });
      }
    }, function () {
      return processResponsePayload(response, body, options.details);
    });
  });
});
function getFileContents_invoke(body, then) {
  var result = body();
  if (result && result.then) {
    return result.then(then);
  }
  return then(result);
}
const getFileContents = getFileContents_async(function (context, filePath) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const {
    format = "binary"
  } = options;
  if (format !== "binary" && format !== "text") {
    throw new Layerr({
      info: {
        code: ErrorCode.InvalidOutputFormat
      }
    }, `Invalid output format: ${format}`);
  }
  return format === "text" ? getFileContentsString(context, filePath, options) : getFileContentsBuffer(context, filePath, options);
});
const TRANSFORM_RETAIN_FORMAT = v => v;
function getFileDownloadLink(context, filePath) {
  let url = joinURL(context.remoteURL, encodePath(filePath));
  const protocol = /^https:/i.test(url) ? "https" : "http";
  switch (context.authType) {
    case AuthType.None:
      // Do nothing
      break;
    case AuthType.Password:
      {
        const authPart = context.headers.Authorization.replace(/^Basic /i, "").trim();
        const authContents = fromBase64(authPart);
        url = url.replace(/^https?:\/\//, `${protocol}://${authContents}@`);
        break;
      }
    default:
      throw new Layerr({
        info: {
          code: ErrorCode.LinkUnsupportedAuthType
        }
      }, `Unsupported auth type for file link: ${context.authType}`);
  }
  return url;
}
;// CONCATENATED MODULE: ./source/tools/xml.ts

function generateLockXML(ownerHREF) {
  return getBuilder().build(namespace({
    lockinfo: {
      "@_xmlns:d": "DAV:",
      lockscope: {
        exclusive: {}
      },
      locktype: {
        write: {}
      },
      owner: {
        href: ownerHREF
      }
    }
  }, "d"));
}
function getBuilder() {
  return new fxp.XMLBuilder({
    attributeNamePrefix: "@_",
    format: true,
    ignoreAttributes: false,
    suppressEmptyNode: true
  });
}
function xml_getParser() {
  return new fxp.XMLParser({
    removeNSPrefix: true,
    parseAttributeValue: true,
    parseTagValue: true
  });
}
function namespace(obj, ns) {
  const copy = {
    ...obj
  };
  for (const key in copy) {
    if (!copy.hasOwnProperty(key)) {
      continue;
    }
    if (copy[key] && typeof copy[key] === "object" && key.indexOf(":") === -1) {
      copy[`${ns}:${key}`] = namespace(copy[key], ns);
      delete copy[key];
    } else if (/^@_/.test(key) === false) {
      copy[`${ns}:${key}`] = copy[key];
      delete copy[key];
    }
  }
  return copy;
}
function parseGenericResponse(xml) {
  return xml_getParser().parse(xml);
}
;// CONCATENATED MODULE: ./source/operations/lock.ts






function lock_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function lock_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const unlock = lock_async(function (context, path, token) {
  let options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(path)),
    method: "UNLOCK",
    headers: {
      "Lock-Token": token
    }
  }, context, options);
  return lock_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
    if (response.status !== 204 && response.status !== 200) {
      const err = createErrorFromResponse(response);
      throw err;
    }
  });
});
const lock = lock_async(function (context, path) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const {
    refreshToken,
    timeout = DEFAULT_TIMEOUT
  } = options;
  const headers = {
    Accept: "text/plain,application/xml",
    Timeout: timeout
  };
  if (refreshToken) {
    headers.If = refreshToken;
  }
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(path)),
    method: "LOCK",
    headers,
    data: generateLockXML(context.contactHref)
  }, context, options);
  return lock_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
    return lock_await(response.text(), function (responseData) {
      const lockPayload = parseGenericResponse(responseData);
      const token = nested_property_default().get(lockPayload, "prop.lockdiscovery.activelock.locktoken.href");
      const serverTimeout = nested_property_default().get(lockPayload, "prop.lockdiscovery.activelock.timeout");
      if (!token) {
        const err = createErrorFromResponse(response, "No lock token received: ");
        throw err;
      }
      return {
        token,
        serverTimeout
      };
    });
  });
});
const DEFAULT_TIMEOUT = "Infinite, Second-4100000000";
;// CONCATENATED MODULE: ./source/tools/quota.ts

function parseQuota(result) {
  try {
    const [responseItem] = result.multistatus.response;
    const {
      propstat: {
        prop: {
          "quota-used-bytes": quotaUsed,
          "quota-available-bytes": quotaAvail
        }
      }
    } = responseItem;
    return typeof quotaUsed !== "undefined" && typeof quotaAvail !== "undefined" ? {
      // As it could be both a string or a number ensure we are working with a number
      used: parseInt(String(quotaUsed), 10),
      available: translateDiskSpace(quotaAvail)
    } : null;
  } catch (err) {
    /* ignore */
  }
  return null;
}
;// CONCATENATED MODULE: ./source/operations/getQuota.ts





function getQuota_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function getQuota_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const getQuota = getQuota_async(function (context) {
  let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  const path = options.path || "/";
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, path),
    method: "PROPFIND",
    headers: {
      Accept: "text/plain,application/xml",
      Depth: "0"
    }
  }, context, options);
  return getQuota_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
    return getQuota_await(response.text(), function (responseData) {
      return getQuota_await(parseXML(responseData), function (result) {
        const quota = parseQuota(result);
        return processResponsePayload(response, quota, options.details);
      });
    });
  });
});
;// CONCATENATED MODULE: ./source/operations/search.ts





function search_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function search_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const getSearch = search_async(function (context, searchArbiter) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const {
    details: isDetailed = false
  } = options;
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(searchArbiter)),
    method: "SEARCH",
    headers: {
      Accept: "text/plain,application/xml",
      // Ensure a Content-Type header is set was this is required by e.g. sabre/dav
      "Content-Type": context.headers["Content-Type"] || "application/xml; charset=utf-8"
    }
  }, context, options);
  return search_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
    return search_await(response.text(), function (responseText) {
      return search_await(parseXML(responseText), function (responseData) {
        const results = parseSearch(responseData, searchArbiter, isDetailed);
        return processResponsePayload(response, results, isDetailed);
      });
    });
  });
});
;// CONCATENATED MODULE: ./source/operations/moveFile.ts




function moveFile_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function moveFile_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const moveFile = moveFile_async(function (context, filename, destination) {
  let options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filename)),
    method: "MOVE",
    headers: {
      Destination: joinURL(context.remoteURL, encodePath(destination)),
      /**
       * From RFC4918 section 10.6: If the overwrite header is not included in a COPY or MOVE request,
       * then the resource MUST treat the request as if it has an overwrite header of value "T".
       *
       * Meaning the overwrite header is always set to "T" EXCEPT the option is explicitly set to false.
       */
      Overwrite: options.overwrite === false ? "F" : "T"
    }
  }, context, options);
  return moveFile_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
  });
});
// EXTERNAL MODULE: ./node_modules/byte-length/dist/index.js
var dist = __webpack_require__(172);
;// CONCATENATED MODULE: ./source/tools/size.ts





function calculateDataLength(data) {
  if (isArrayBuffer(data)) {
    return data.byteLength;
  } else if (isBuffer(data)) {
    return data.length;
  } else if (typeof data === "string") {
    return (0,dist/* byteLength */.d)(data);
  }
  throw new Layerr({
    info: {
      code: ErrorCode.DataTypeNoLength
    }
  }, "Cannot calculate data length: Invalid type");
}
;// CONCATENATED MODULE: ./source/operations/putFileContents.ts










function putFileContents_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function putFileContents_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const putFileContents = putFileContents_async(function (context, filePath, data) {
  let options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
  const {
    contentLength = true,
    overwrite = true
  } = options;
  const headers = {
    "Content-Type": "application/octet-stream"
  };
  if (!env_isWeb() && !isReactNative() && typeof (stream_ignored_default_0()) !== "undefined" && typeof (stream_ignored_default_0())?.Readable !== "undefined" && data instanceof (stream_ignored_default_0()).Readable) {
    // Skip, no content-length
  } else if (contentLength === false) {
    // Skip, disabled
  } else if (typeof contentLength === "number") {
    headers["Content-Length"] = `${contentLength}`;
  } else {
    headers["Content-Length"] = `${calculateDataLength(data)}`;
  }
  if (!overwrite) {
    headers["If-None-Match"] = "*";
  }
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filePath)),
    method: "PUT",
    headers,
    data
  }, context, options);
  return putFileContents_await(request(requestOptions, context), function (response) {
    try {
      handleResponseCode(context, response);
    } catch (err) {
      const error = err;
      if (error.status === 412 && !overwrite) {
        return false;
      } else {
        throw error;
      }
    }
    return true;
  });
});
function getFileUploadLink(context, filePath) {
  let url = `${joinURL(context.remoteURL, encodePath(filePath))}?Content-Type=application/octet-stream`;
  const protocol = /^https:/i.test(url) ? "https" : "http";
  switch (context.authType) {
    case AuthType.None:
      // Do nothing
      break;
    case AuthType.Password:
      {
        const authPart = context.headers.Authorization.replace(/^Basic /i, "").trim();
        const authContents = fromBase64(authPart);
        url = url.replace(/^https?:\/\//, `${protocol}://${authContents}@`);
        break;
      }
    default:
      throw new Layerr({
        info: {
          code: ErrorCode.LinkUnsupportedAuthType
        }
      }, `Unsupported auth type for file link: ${context.authType}`);
  }
  return url;
}
;// CONCATENATED MODULE: ./source/operations/getDAVCompliance.ts




function getDAVCompliance_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
function getDAVCompliance_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const getDAVCompliance = getDAVCompliance_async(function (context, filePath) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filePath)),
    method: "OPTIONS"
  }, context, options);
  return getDAVCompliance_await(request(requestOptions, context), function (response) {
    try {
      handleResponseCode(context, response);
    } catch (err) {
      const error = err;
      throw error;
    }
    const davHeader = response.headers.get("DAV") ?? "";
    const compliance = davHeader.split(",").map(item => item.trim());
    const server = response.headers.get("Server") ?? "";
    return {
      compliance,
      server
    };
  });
});
;// CONCATENATED MODULE: ./source/operations/partialUpdateFileContents.ts







function partialUpdateFileContents_await(value, then, direct) {
  if (direct) {
    return then ? then(value) : value;
  }
  if (!value || !value.then) {
    value = Promise.resolve(value);
  }
  return then ? value.then(then) : value;
}
const partialUpdateFileContentsApache = partialUpdateFileContents_async(function (context, filePath, start, end, data) {
  let options = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};
  if (start > end || start < 0) {
    throw new Layerr({
      info: {
        code: ErrorCode.InvalidUpdateRange
      }
    }, `Invalid update range ${start} for partial update`);
  }
  const headers = {
    "Content-Type": "application/octet-stream",
    "Content-Length": `${end - start + 1}`,
    "Content-Range": `bytes ${start}-${end}/*`
  };
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filePath)),
    method: "PUT",
    headers,
    data
  }, context, options);
  return partialUpdateFileContents_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
  });
});
function partialUpdateFileContents_invoke(body, then) {
  var result = body();
  if (result && result.then) {
    return result.then(then);
  }
  return then(result);
}
const partialUpdateFileContentsSabredav = partialUpdateFileContents_async(function (context, filePath, start, end, data) {
  let options = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};
  if (start > end || start < 0) {
    // Actually, SabreDAV support negative start value,
    // Do not support here for compatibility with Apache-style way
    throw new Layerr({
      info: {
        code: ErrorCode.InvalidUpdateRange
      }
    }, `Invalid update range ${start} for partial update`);
  }
  const headers = {
    "Content-Type": "application/x-sabredav-partialupdate",
    "Content-Length": `${end - start + 1}`,
    "X-Update-Range": `bytes=${start}-${end}`
  };
  const requestOptions = prepareRequestOptions({
    url: joinURL(context.remoteURL, encodePath(filePath)),
    method: "PATCH",
    headers,
    data
  }, context, options);
  return partialUpdateFileContents_await(request(requestOptions, context), function (response) {
    handleResponseCode(context, response);
  });
});
function partialUpdateFileContents_async(f) {
  return function () {
    for (var args = [], i = 0; i < arguments.length; i++) {
      args[i] = arguments[i];
    }
    try {
      return Promise.resolve(f.apply(this, args));
    } catch (e) {
      return Promise.reject(e);
    }
  };
}
const partialUpdateFileContents = partialUpdateFileContents_async(function (context, filePath, start, end, data) {
  let options = arguments.length > 5 && arguments[5] !== undefined ? arguments[5] : {};
  return partialUpdateFileContents_await(getDAVCompliance(context, filePath, options), function (compliance) {
    let _exit = false;
    return partialUpdateFileContents_invoke(function () {
      if (compliance.compliance.includes("sabredav-partialupdate")) {
        return partialUpdateFileContents_await(partialUpdateFileContentsSabredav(context, filePath, start, end, data, options), function (_await$partialUpdateF) {
          _exit = true;
          return _await$partialUpdateF;
        });
      }
    }, function (_result) {
      let _exit2 = false;
      if (_exit) return _result;
      return partialUpdateFileContents_invoke(function () {
        if (compliance.server.includes("Apache") && compliance.compliance.includes("<http://apache.org/dav/propset/fs/1>")) {
          return partialUpdateFileContents_await(partialUpdateFileContentsApache(context, filePath, start, end, data, options), function (_await$partialUpdateF2) {
            _exit2 = true;
            return _await$partialUpdateF2;
          });
        }
      }, function (_result2) {
        if (_exit2) return _result2;
        throw new Layerr({
          info: {
            code: ErrorCode.NotSupported
          }
        }, "Not supported");
      });
    });
  });
});
;// CONCATENATED MODULE: ./source/factory.ts



















const DEFAULT_CONTACT_HREF = "https://github.com/perry-mitchell/webdav-client/blob/master/LOCK_CONTACT.md";
function createClient(remoteURL) {
  let options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  const {
    authType: authTypeRaw = null,
    remoteBasePath,
    contactHref = DEFAULT_CONTACT_HREF,
    ha1,
    headers = {},
    httpAgent,
    httpsAgent,
    password,
    token,
    username,
    withCredentials
  } = options;
  let authType = authTypeRaw;
  if (!authType) {
    authType = username || password ? AuthType.Password : AuthType.None;
  }
  const context = {
    authType,
    remoteBasePath,
    contactHref,
    ha1,
    headers: Object.assign({}, headers),
    httpAgent,
    httpsAgent,
    password,
    remotePath: extractURLPath(remoteURL),
    remoteURL,
    token,
    username,
    withCredentials
  };
  setupAuth(context, username, password, token, ha1);
  return {
    copyFile: (filename, destination, options) => copyFile(context, filename, destination, options),
    createDirectory: (path, options) => createDirectory(context, path, options),
    createReadStream: (filename, options) => createReadStream(context, filename, options),
    createWriteStream: (filename, options, callback) => createWriteStream(context, filename, options, callback),
    customRequest: (path, requestOptions) => customRequest(context, path, requestOptions),
    deleteFile: (filename, options) => deleteFile(context, filename, options),
    exists: (path, options) => exists(context, path, options),
    getDirectoryContents: (path, options) => getDirectoryContents(context, path, options),
    getFileContents: (filename, options) => getFileContents(context, filename, options),
    getFileDownloadLink: filename => getFileDownloadLink(context, filename),
    getFileUploadLink: filename => getFileUploadLink(context, filename),
    getHeaders: () => Object.assign({}, context.headers),
    getQuota: options => getQuota(context, options),
    lock: (path, options) => lock(context, path, options),
    moveFile: (filename, destinationFilename, options) => moveFile(context, filename, destinationFilename, options),
    putFileContents: (filename, data, options) => putFileContents(context, filename, data, options),
    partialUpdateFileContents: (filePath, start, end, data, options) => partialUpdateFileContents(context, filePath, start, end, data, options),
    getDAVCompliance: path => getDAVCompliance(context, path),
    search: (path, options) => getSearch(context, path, options),
    setHeaders: headers => {
      context.headers = Object.assign({}, headers);
    },
    stat: (path, options) => getStat(context, path, options),
    unlock: (path, token, options) => unlock(context, path, token, options)
  };
}
;// CONCATENATED MODULE: ./source/index.ts





var __webpack_exports__AuthType = __webpack_exports__.hT;
var __webpack_exports__ErrorCode = __webpack_exports__.O4;
var __webpack_exports__Request = __webpack_exports__.Kd;
var __webpack_exports__Response = __webpack_exports__.YK;
var __webpack_exports__createClient = __webpack_exports__.UU;
var __webpack_exports__getPatcher = __webpack_exports__.Gu;
var __webpack_exports__parseStat = __webpack_exports__.ky;
var __webpack_exports__parseXML = __webpack_exports__.h4;
var __webpack_exports__prepareFileFromProps = __webpack_exports__.ch;
var __webpack_exports__processResponsePayload = __webpack_exports__.hq;
var __webpack_exports__translateDiskSpace = __webpack_exports__.i5;
export { __webpack_exports__AuthType as AuthType, __webpack_exports__ErrorCode as ErrorCode, __webpack_exports__Request as Request, __webpack_exports__Response as Response, __webpack_exports__createClient as createClient, __webpack_exports__getPatcher as getPatcher, __webpack_exports__parseStat as parseStat, __webpack_exports__parseXML as parseXML, __webpack_exports__prepareFileFromProps as prepareFileFromProps, __webpack_exports__processResponsePayload as processResponsePayload, __webpack_exports__translateDiskSpace as translateDiskSpace };
