function applyOuterCSS() {
    if (console.log(">>>>>>>>>>>>> start excute applyOuterCSS"), !document.getElementById(LINK_ELEMENT_ID)) {
        var e = mbrowser.getInjectCss();
        css = document.createElement("link"), css.id = LINK_ELEMENT_ID, css.rel = "stylesheet", css.href = e;
        var t = document.getElementsByTagName("head");
        if (t.length > 0 && t[0].appendChild(css), !document.getElementById(STYLE_ELEMENT_ID)) {
            var o = document.createElement("style");
            o.type = "text/css", o.rel = "stylesheet", o.id = STYLE_ELEMENT_ID, o.appendChild(document.createTextNode(e)), null != document.documentElement && document.documentElement.appendChild(o)
        }
    }
}

function appExtraCss() {
    if (!document.getElementById(LINK_EXTRA_CSS_ID)) {
        var e = "data:text/css,html,body,a,div,p,img,textarea{-webkit-touch-callout:text !important;-webkit-user-select:text !important;user-select:text !important;}";
        css = document.createElement("link"), css.id = LINK_EXTRA_CSS_ID, css.rel = "stylesheet", css.href = e;
        var t = document.getElementsByTagName("head");
        if (t.length > 0 && t[0].appendChild(css), !document.getElementById(STYLE_EXTRA_CSS_ID)) {
            var o = document.createElement("style");
            o.type = "text/css", o.rel = "stylesheet", o.id = STYLE_EXTRA_CSS_ID, o.appendChild(document.createTextNode(e)), null != document.documentElement && document.documentElement.appendChild(o)
        }
    }
}

function view_source() {
    var e = document.documentElement.outerHTML;
    mbrowser.viewSource(e)
}

function disableOuterCSS() {
    var e = document.getElementById(LINK_ELEMENT_ID);
    e && e.parentNode.removeChild(e);
    var t = document.getElementById(STYLE_ELEMENT_ID);
    t && t.parentNode.removeChild(t)
}

function notifyHitTest(e, t) {
    var o = {};
    o.tagType = e, o.src = t, mbrowser.onElementHitTest(JSON.stringify(o))
}

function getTop(e) {
    var t = e.offsetTop;
    return null != e.offsetParent && (t += getTop(e.offsetParent)), t
}

function getLeft(e) {
    var t = e.offsetLeft;
    return null != e.offsetParent && (t += getLeft(e.offsetParent)), t
}

function native_call_fetch_image_res() {
    for (var e = document.querySelectorAll("img"), t = 0; t < e.length; t++) {
        var o = e[t];
        if (o.width >= 200 && o.height >= 100 || o.height >= 200) {
            var n = o.parentNode;
            if (n && "a" == n.tagName.toLowerCase() && (n.href.indexOf(".png") > 0 || n.href.indexOf(".jpg") > 0)) {
                mbrowser.addImageRes("", n.href);
                continue
            }
            if (o.src.indexOf("data:image") >= 0) continue;
            mbrowser.addImageRes(o.alt, o.src)
        }
    }
}

function native_call_add_tag_to_resource() {
    for (var e = document.querySelectorAll("img"), t = 0; t < e.length; t++) mbrowser.addTagToResource(e[t].src, "img");
    for (var o = document.querySelectorAll("script"), t = 0; t < o.length; t++) mbrowser.addTagToResource(o[t].src, "js");
    for (var n = document.querySelectorAll("link"), t = 0; t < n.length; t++) {
        var r = n[t].href;
        r.indexOf("ico") > 0 || r.indexOf("png") > 0 || r.indexOf("jpg") > 0 ? mbrowser.addTagToResource(n[t].href, "img") : mbrowser.addTagToResource(n[t].href, "css")
    }
    for (var a = document.querySelectorAll("video"), t = 0; t < a.length; t++) mbrowser.addTagToResource(a[t].src, "video");
    for (var i = document.querySelectorAll("audio"), t = 0; t < i.length; t++) mbrowser.addTagToResource(i[t].src, "audio")
}

function native_call_sniff_media_res() {
    for (var e = document.querySelectorAll("video"), t = 0; t < e.length; t++) mbrowser.addTagToResource(e[t].src, "video");
    for (var o = document.querySelectorAll("audio"), t = 0; t < o.length; t++) mbrowser.addTagToResource(o[t].src, "audio");
    mbrowser.notifySniffMediaRes()
}

function get_client_height() {
    var e = 0;
    if (document.body.clientHeight && document.documentElement.clientHeight) var e = document.body.clientHeight < document.documentElement.clientHeight ? document.body.clientHeight : document.documentElement.clientHeight; else var e = document.body.clientHeight > document.documentElement.clientHeight ? document.body.clientHeight : document.documentElement.clientHeight;
    return e
}

function is_touch_on_element(e, t, o) {
    if (e && e.offsetHeight) {
        var n = getLeft(e), r = n + e.clientWidth, a = getTop(e), i = a + e.clientHeight;
        return console.log("x:" + t + " y:" + o), console.log("tagName:" + e.tagName + "clientHeight:" + e.clientHeight + "(l:" + n + ",r:" + r + ",t:" + a + ",b:" + i + ")"), t >= n && r >= t && o >= a && i >= o
    }
    return !1
}

function _hit_test(e) {
    var e = e || window.event, t = e.target, o = e.touches[0];
    return do_hit(o.pageX, o.pageY, t), !1
}

function tranx_has_set(e) {
    if (e.style) {
        var t = e.style.transform;
        if (t || (t = e.style.webkitTransform), t && (console.log("###can transform" + e.style.transform + " webkit-tran:" + e.style.webkitTransform), t.indexOf("(0px,") < 0)) return console.log(">>>> element has trans........."), !0
    }
    return !1
}

function checkChildCanSwipe(e) {
    console.log(">>>###  do check target tran:" + e.tagName + " class:" + tagName["class"]);
    var t = !1;
    if (tranx_has_set(e)) return !0;
    for (var o = 0; o < e.childElementCount; o++) {
        var n = e.children[o];
        if (t = checkChildCanSwipe(n)) break
    }
    return t
}

function bind_li_data(e, t) {
    for (; e.parentNode && (console.log(">>>> touch tag name:" + e.tagName), "li" != e.tagName.toLowerCase());) e = e.parentNode;
    e && "li" == e.tagName.toLowerCase() && (t.tagType = "li", t.li_id = e.getAttribute("id"), t.data_url = e.getAttribute("data-url"), t.data_title = e.getAttribute("data-title"), t.data_type = e.getAttribute("data-type"), notify_got_hit_data(t))
}

function notify_got_hit_data(e) {
    mbrowser.onElementHitTest(JSON.stringify(e))
}

function do_hit(e, t, o) {
    console.log("Touch down >>>>>>>>>>>>>>>>>>>>>>>>>" + o.tagName + "class:" + o.parentNode["class"] + " id:" + o.id + " sytyle:" + o.style + " event" + o.eventList);
    var n = {};
    if ("img" == o.tagName.toLowerCase()) n.source = "normal", n.tagType = "img", n.src = o.src, notify_got_hit_data(n); else if (document.location.host.indexOf("facebook") >= 0) {
        var r = !1, a = document.querySelectorAll("i.img[data-store]");
        for (i in a) {
            var l = a[i];
            if (is_touch_on_element(e, t)) {
                JSON.parse(l.getAttribute("data-store"));
                n.source = "facebook", n.tagType = "img", n.src = o.src, notify_got_hit_data(n), touch_img = !0
            }
        }
        if (!r) {
            a = document.querySelectorAll("i.img[style]"), console.log(" >>>> totlal image : " + a.length);
            for (i in a) {
                var l = a[i];
                if (is_touch_on_element(l, e, t) && l.style.backgroundImage) {
                    var c = l.style.backgroundImage;
                    console.log("sniff src:" + l.style.backgroundImage);
                    var s = /url\(\"(.*)\"\)/, d = c.match(s);
                    if (d.length > 1) {
                        console.log("found the image element .." + d[1]), n.source = "facebook", n.tagType = "img", n.src = o.src, notify_got_hit_data(n);
                        break
                    }
                }
            }
        }
    } else if (document.location.host.indexOf("instagram") >= 0) {
        var a = document.querySelectorAll("img");
        console.log(" totlal image : " + a.length);
        for (i in a) {
            var l = a[i];
            if (is_touch_on_element(l, e, t)) {
                n.source = "instagram", n.tagType = "img", n.src = o.src, notify_got_hit_data(notify_got_hit_data);
                break
            }
        }
    } else if (document.location.href.indexOf("file:///android_asset") >= 0) try {
        bind_li_data(o, n)
    } catch (u) {
        console.log("catch bind_li_data error")
    }
}

function check_swipe_element(e) {
    if (mbrowser.elementCanSwipe(!1), "iframe" == e.tagName.toLowerCase()) try {
        checkChildCanSwipe(e.contentDocument) && mbrowser.elementCanSwipe(!0)
    } catch (t) {
        mbrowser.elementCanSwipe(!0), console.log(t.message)
    } else for (; e.parentNode;) {
        if (tranx_has_set(e)) {
            mbrowser.elementCanSwipe(!0);
            break
        }
        e = e.parentNode
    }
    console.log("windows.scrollX:" + window.scrollX), window.scrollX > 0 && mbrowser.elementCanSwipe(!0)
}

function clean_video_control_style() {
    var e = document.getElementById("video-style-control");
    e && e.parentElement && e.parentElement.removeChild(e)
}

function pause_or_play() {
    CURRENT_VIDEO && (CURRENT_VIDEO.paused ? CURRENT_VIDEO.play() : CURRENT_VIDEO.pause())
}

function set_video_seek(e) {
    CURRENT_VIDEO && (CURRENT_VIDEO.currentTime = e)
}

function play_with_fullscreen() {
    CURRENT_VIDEO && CURRENT_VIDEO.webkitRequestFullScreen()
}

function bind_video_events(e) {
    e.addEventListener("play", function () {
        console.log(">>>>> the new video played:" + this.src), CURRENT_VIDEO = this, mbrowser.notifyVideoPlayed(this.src)
    }), e.addEventListener("loadedmetadata", function () {
        mbrowser.notifyVideoLoad(this.src, e.duration)
    }), e.addEventListener("timeupdate", function () {
        mbrowser.notifyVideoTimeUpdate(e.currentTime, e.duration)
    }), e.addEventListener("pause", function () {
        mbrowser.notifyVideoPaused()
    })
}

function sniff_video() {
    var e = !1;
    try {
        var t = document.querySelectorAll("video");
        console.log(">>>> found :" + t.length + " videos");
        for (var o = 0; o < t.length; o++) {
            var n = t[o];
            bind_video_events(n), e = !0
        }
        if (0 == t.length) for (var r = document.querySelectorAll("iframe"), a = 0; a < r.length; a++) {
            var i = r[a], l = i.contentDocument.querySelector("video");
            l && (bind_video_events(l), e = !0)
        }
    } catch (c) {
        console.log(c)
    }
    e && mbrowser.notifyHasVideoTag()
}

function cancel_select() {
    var e = window.getSelection();
    if (e.removeAllRanges(), e = null, mbrowser.log(">>>>>>> exit select state >>>>>>>>>>>"), last_touch_a_element) {
        var t = last_touch_a_element.getAttribute("_href");
        last_touch_a_element.removeAttribute("_href"), t && last_touch_a_element.setAttribute("href", t)
    }
}

function get_a_element_by_touch(e) {
    if (1 == e.nodeType) {
        var t = e.querySelector("a");
        if (null != t) return t;
        var o = e.tagName.toLowerCase();
        if ("a" == o) return e;
        var n = e.parentNode;
        return n ? get_a_element_by_touch(n) : null
    }
    var r = e.parentNode;
    return r ? get_a_element_by_touch(r) : null
}

function get_text_elemt(e) {
    if (3 == e.nodeType) return e.parentNode;
    for (var t = e.childNodes, o = 0; o < t.length; o++) {
        var n = t[o];
        mbrowser.log(">>>>> travel child: " + n.nodeName);
        var r = get_text_elemt(n);
        if (null != r) return r
    }
    return null
}

function select_text2(e, t, o, n) {
    var r = e * (window.innerWidth / n), a = t * (window.innerHeight / o), i = document.elementFromPoint(r, a),
        l = window.getSelection();
    l.removeAllRanges(), l.selectAllChildren(i)
}

function select_text(e, t, o, n) {
    var r = e * (window.innerWidth / n), a = t * (window.innerHeight / o);
    mbrowser.log(">>>> dx:" + e + ">>>> dy:" + t), mbrowser.log(">>>> px:" + r + ">>>>> py:" + a);
    var i = r * (n / window.innerWidth), l = a * (o / window.innerHeight);
    mbrowser.log(">>>> rx:" + i + ">>>> ry:" + l);
    var c = document.elementFromPoint(r, a), s = c.tagName.toLowerCase();
    if (mbrowser.log("########### try rewrite href element:#########" + c.tagName + " class:" + c.className), last_touch_a_element = get_a_element_by_touch(c)) {
        mbrowser.log(">>>>>>>>>>>>>>>found a elements");
        var d = last_touch_a_element.getAttribute("href");
        if (d && (last_touch_a_element.removeAttribute("href"), last_touch_a_element.setAttribute("_href", d)), mbrowser.log(">>>>>>>>>>> elemet html:" + last_touch_a_element.outerHTML), "img" == s) {
            var u = get_text_elemt(last_touch_a_element), m = window.getSelection();
            return m.removeAllRanges(), void m.selectAllChildren(u)
        }
    } else mbrowser.log(">>>>>>>>>>>>>>> not found a elements ");
    mbrowser.sendLongPress(e, t)
}

function native_touch_to_element(e, t, o, n) {
    e *= window.innerWidth / n, t *= window.innerHeight / o;
    var r = document.elementFromPoint(e, t);
    return r
}

function hit_swipe_element(e, t, o, n) {
    var r = native_touch_to_element(e, t, o, n);
    check_swipe_element(r)
}

function hit_test(e, t, o, n) {
    var r = native_touch_to_element(e, t, o, n);
    do_hit(e, t, r), mbrowser.adBlockActived() && hit_ad_test(e, t, o, n)
}

function fetch_value_by_name(e, t) {
    for (var o = 0; o < e.length; o++) {
        var n = e[o];
        if (n.name == t) return n.value
    }
}

function fetch_value_by_id(e, t) {
    for (var o = 0; o < e.length; o++) {
        var n = e[o];
        if (n.id == t) return n.value
    }
}

function is_auto_fill_data(e) {
    var t = ["pass", "name", "usr", "user", "addr", "mail", "nick", "phone", "account"];
    if (e) for (var o = 0; o < t.length; o++) if (e.indexOf(t[o]) >= 0) return !0;
    return !1
}

function auto_fill_form() {
    var e = document.querySelectorAll("input"), t = null, o = null;
    if (e) {
        console.log("======== read form data: found total:total " + e.length);
        for (var n = 0; n < e.length; n++) {
            var r = e[n].name, a = e[n].id;
            if (r && (r = r.toLowerCase()), a && (a = a.toLowerCase()), console.log("======== read form data:[name]" + r), (is_auto_fill_data(r) || is_auto_fill_data(a)) && (t || (t = mbrowser.loadFormData(window.location.host), console.log("============ read form data>>>>" + t), o = JSON.parse(t)), o)) {
                var i = fetch_value_by_name(o, r);
                i || (i = fetch_value_by_id(o, a)), console.log("============ read form data : " + i + "============="), i && i.length > 0 && (e[n].value = i, e[n].style.background = "#F9FBBF")
            }
        }
    }
}

function install_submit_hook() {
    var e = document.querySelector("form");
    if (e) {
        var t = e.querySelectorAll("input");
        if (t && t.length > 1) {
            var o = e.querySelector("button");
            o || (o = e.querySelector(".btn")), o || (o = e.querySelector("a")), o && o.addEventListener("click", function () {
                on_submit(), has_submit = !0
            }, !1)
        }
    }
}

function on_submit() {
    for (var e = document.querySelectorAll("input"), t = [], o = 0; o < e.length; o++) {
        var n = {};
        if (e[o].name) {
            var r = e[o].name.toLowerCase();
            if (console.log(">>>>>>>form data: [name]" + r), is_auto_fill_data(r)) {
                n.name = r;
                var a = e[o].value;
                void 0 != a ? n.value = a : n.value = "", t.push(n)
            }
        }
    }
    if (0 == t.length) {
        console.log("form data >>>>> try id attr >>>>>>>>>>>>");
        for (var o = 0; o < e.length; o++) {
            var n = {};
            if (e[o].id) {
                var i = e[o].id.toLowerCase();
                if (console.log(">>>>>>>form data:" + i), is_auto_fill_data(i)) {
                    n.id = i;
                    var a = e[o].value;
                    void 0 != a ? n.value = a : n.value = "", t.push(n)
                }
            }
        }
    }
    if (t.length > 0) {
        var l = JSON.stringify(t);
        mbrowser.onSubmitData(window.location.host, l)
    }
}

function _b(e, t) {
    for (var o = 0; o < t.length - 2; o += 3) {
        var n = t.charAt(o + 2), n = n >= "a" ? n.charCodeAt(0) - 87 : Number(n),
            n = "+" == t.charAt(o + 1) ? e >>> n : e << n;
        e = "+" == t.charAt(o) ? e + n & 4294967295 : e ^ n
    }
    return e
}

function tk(e) {
    for (var t = TKK.split("."), o = Number(t[0]) || 0, n = [], r = 0, a = 0; a < e.length; a++) {
        var i = e.charCodeAt(a);
        128 > i ? n[r++] = i : (2048 > i ? n[r++] = i >> 6 | 192 : (55296 == (64512 & i) && a + 1 < e.length && 56320 == (64512 & e.charCodeAt(a + 1)) ? (i = 65536 + ((1023 & i) << 10) + (1023 & e.charCodeAt(++a)), n[r++] = i >> 18 | 240, n[r++] = i >> 12 & 63 | 128) : n[r++] = i >> 12 | 224, n[r++] = i >> 6 & 63 | 128), n[r++] = 63 & i | 128)
    }
    for (e = o, r = 0; r < n.length; r++) e += n[r], e = _b(e, "+-a^+6");
    return e = _b(e, "+-3^+b+-f"), e ^= Number(t[1]) || 0, 0 > e && (e = (2147483647 & e) + 2147483648), e %= 1e6, e.toString() + "." + (e ^ o)
}

function needInjectCss() {
    return mbrowser.needInjectCss() && document.location.href.indexOf("article_list_for_xb_readmode") < 0
}

function applyToDesktopMode(e) {
    var t = document.querySelector('meta[name="viewport"]');
    t && (e ? t.content = "width=1080" : t.content = "width=device-width initial-scale=1.0")
}

var LINK_ELEMENT_ID = "x_link_element_id", STYLE_ELEMENT_ID = "x_style_element_id",
    LINK_EXTRA_CSS_ID = "x_link_extra_css_id", STYLE_EXTRA_CSS_ID = "x_style_extra_css_id", CURRENT_VIDEO, has_submit,
    in_selection = !1, last_touch_a_element = null, call_timeout, TKK = function () {
        var e = 561666268, t = 1526272306;
        return "406398." + (e + t)
    }();
!function () {
    needInjectCss() && (applyOuterCSS(), mbrowser.onOuterCSSApplied()), appExtraCss(), console.log("============  start install nightmode js ==================="), document.addEventListener("DOMContentLoaded", function () {
        for (var e = document.querySelectorAll("iframe"), t = 0; t < e.length; t++) e[t].setAttribute("allowfullscreen", !0);
        mbrowser.onDOMContentLoaded(), console.log("============  dom load on night ==================="), needInjectCss() && (applyOuterCSS(), mbrowser.onOuterCSSApplied()), sniff_video(), mbrowser.autoSavePasswd() && (install_submit_hook(), auto_fill_form())
    }, !1), window.addEventListener("load", function () {
        mbrowser.onPageLoaded(), console.log("============  page load on night ==================="), mbrowser.inNightMode() && applyOuterCSS(), sniff_video(), mbrowser.autoSavePasswd() && (install_submit_hook(), auto_fill_form())
    }, !1), window.addEventListener("submit", function () {
        !has_submit && mbrowser.autoSavePasswd() && on_submit()
    }, !1), document.onselectionchange = function () {
        var e = window.getSelection().toString();
        if (e && e.length > 0) {
            in_selection = !0;
            var t = tk(e);
            mbrowser.onSelectTextChange(e, t)
        } else console.log("in_selection:" + in_selection), 1 == in_selection && (in_selection = !1, mbrowser.onSelectTextChange(e, ""))
    }
}();