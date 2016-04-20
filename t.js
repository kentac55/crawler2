'use strict';
var fs = require('fs'),
  cheerio = require('cheerio'),
  dir = './temp/moae.jp_webcommic/',
  obj = {
    'id': 'moae.jp_webcommic',
    'props': {
      'siteName': {
        'layer': '1',
        'selector': '',
        'args': 'text',
        'script': function() {
          return 'moae';
        }
      },
      'tag': {
        'layer': '1',
        'selector': '',
        'args': 'text',
        'script': function() {
          return [];
        }
      },
      'backNum_text': {
        'layer': '1',
        'selector': '.fake',
        'args': 'text',
        'script': false
      },
      'backNum_url': {
        'layer': '1',
        'selector': '.fake',
        'args': 'href',
        'script': false
      },
      'title': {
        'layer': '0',
        'selector': '.book-title',
        'args': 'text',
        'script': false
      },
      'auther': {
        'layer': '0',
        'selector': '.book-author',
        'args': 'text',
        'script': false
      },
      'magazine': {
        'layer': '0',
        'selector': '.book-magazine',
        'args': 'text',
        'script': false
      },
      'new': {
        'layer': '0',
        'selector': '.book-date',
        'args': 'text',
        'script': false
      },
      'next': {
        'layer': '1',
        'selector': '.fake',
        'args': 'text',
        'script': false
      },
      'imageUrl': {
        'layer': '1',
        'selector': '.profile-photo > img',
        'args': 'src',
        'script': false
      }
    }
  };

exports.m = function(id, o, t) {
  var result = {},
    $ = [],
    temp,
    p = '',
    text;

  // 入力されたidと定義のidが異なったら例外
  if (id !== obj.id) {
    console.log(id, obj.props.id);
    throw new Error('id不一致');
  }

  for (p in o) {

    // おまじない
    result[p] = {};

    // 破棄urlの処理
    if (p === '_trash') {
      console.log(o[p].length, '個のうｒｌを破棄');
      result[p] = o[p];
      continue;
    }

    // htmlをcheerioへload
    for (var i = 0; i < o[p].length; i++) {
      temp = fs.readFileSync(dir + o[p][i]);
      $[i] = cheerio.load(temp);
    }

    // 出力データを生成
    for (var propName in t) {

      // 取得argsがtrueの時のみ実行
      if (obj.props[propName].args) {

        // textを取るかargsを取るか
        if (obj.props[propName].args === 'text') {
          text = $[obj.props[propName].layer](obj.props[propName].selector).text();
        } else {
          text = $[obj.props[propName].layer](obj.props[propName].selector).attr(obj.props[propName].args);
        }
      }

      // scriptがtrueだった場合scriptを実行
      if (obj.props[propName].script) {
        result[p][propName] = obj.props[propName].script(text);
      } else {
        result[p][propName] = text;
      }
    }
  }
  return result;
};