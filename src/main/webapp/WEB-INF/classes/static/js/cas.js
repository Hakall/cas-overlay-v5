
head.ready(document, function() {
    if (!window.jQuery) {
        head.load("https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js", loadjQueryUI);
    } else {
        resourceLoadedSuccessfully();
    }
});

function loadjQueryUI() {
    head.load("https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js", loadjQueryCookies);
}

function loadjQueryCookies() {
    head.load("https://cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.min.js", resourceLoadedSuccessfully);
}

function requestGeoPosition() {
    console.log("Requesting GeoLocation data from the browser...");
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showGeoPosition, logGeoLocationError,
            {maximumAge:600000, timeout:5000, enableHighAccuracy: true});
    } else {
        console.log("Browser does not support Geo Location");
    }
}

function logGeoLocationError(error) {
    switch(error.code) {
        case error.PERMISSION_DENIED:
            console.log("User denied the request for Geolocation.");
            break;
        case error.POSITION_UNAVAILABLE:
            console.log("Location information is unavailable.");
            break;
        case error.TIMEOUT:
            console.log("The request to get user location timed out.");
            break;
        default:
            console.log("An unknown error occurred.");
            break;
    }
}

function showGeoPosition(position) {
    $('[name="geolocation"]').val(position.coords.latitude + ","
        + position.coords.longitude + "," + position.coords.accuracy + "," + position.timestamp);
}

function areCookiesEnabled() {
    $.cookie('cookiesEnabled', 'true');
    var value = $.cookie('cookiesEnabled');
    if (value != undefined) {
        $.removeCookie('cookiesEnabled');
        return true;
    }
    return false;
}

function resourceLoadedSuccessfully() {
    $(document).ready(function() {

        if (trackGeoLocation) {
            requestGeoPosition();
        }

        if ($(":focus").length === 0){
            $("input:visible:enabled:first").focus();
        }


        if (areCookiesEnabled()) {
            $('#cookiesDisabled').hide();
        } else {
            $('#cookiesDisabled').show();
            $('#cookiesDisabled').animate({ backgroundColor: 'rgb(187,0,0)' }, 30).animate({ backgroundColor: 'rgb(255,238,221)' }, 500);
        }

        //flash error box
        $('#msg.errors').animate({ backgroundColor: 'rgb(187,0,0)' }, 30).animate({ backgroundColor: 'rgb(255,238,221)' }, 500);

        //flash success box
        $('#msg.success').animate({ backgroundColor: 'rgb(51,204,0)' }, 30).animate({ backgroundColor: 'rgb(221,255,170)' }, 500);

        //flash confirm box
        $('#msg.question').animate({ backgroundColor: 'rgb(51,204,0)' }, 30).animate({ backgroundColor: 'rgb(221,255,170)' }, 500);

        $('#capslock-on').hide();
        $('#password').keypress(function(e) {
            var s = String.fromCharCode( e.which );
            if ( s.toUpperCase() === s && s.toLowerCase() !== s && !e.shiftKey ) {
                $('#capslock-on').show();
            } else {
                $('#capslock-on').hide();
            }
        });
        if (typeof(jqueryReady) == "function") {
            jqueryReady();
        }
    });

};

//ESUP-OTP 
window.onload = parseUserInfos();
var userInfos;
var code_send = false;
var last_transport = '';

function submit() {
    document.getElementById("submit").click();
}

function request(opts, callback, next) {
    var req = new XMLHttpRequest();
    req.open(opts.method, opts.url, true);
    req.onerror = function(e) {
        console.log(e);
        code_send = false;
    };
    req.onreadystatechange = function(aEvt) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var responseObject = JSON.parse(req.responseText);
                if (typeof(callback) === "function") callback(responseObject);
            }
            if (typeof(next) === "function") next();
        }
    };
    req.send(null);
}

function parseUserInfos() {
    userInfos = JSON.parse(document.getElementById("userInfos").innerHTML);
    parseUserMethods();
}

function parseUserMethods() {
    for (method in userInfos.user.methods) {
        if (userInfos.user.methods[method].active) parseMethodTransports(method);
    }
}

function parseMethodTransports(method) {
    if (userInfos.user.methods[method].transports.indexOf('sms') >= 0 || userInfos.user.methods[method].transports.indexOf('mail') >= 0) {
        $('#list-methods').append("<div class='row' id='" + method + "_method'><h4>" + method + "</h4></div>");
        if (userInfos.user.methods[method].transports.indexOf('sms') >= 0 && userInfos.user.transports.sms) $('#list-methods').append("<div class='method-row sms'><input class='button transport label-sms' type='button' value='" + userInfos.user.transports.sms + " &#xf10b;' onclick='send_code(\"sms\", \"" + method + "\");'></div>");
        if (userInfos.user.methods[method].transports.indexOf('mail') >= 0 && userInfos.user.transports.mail) $('#list-methods').append("<div class='method-row mail'><input class='button transport label-mail' type='button' value='" + userInfos.user.transports.mail + " &#xf0e0;' onclick='send_code(\"mail\", \"" + method + "\");'></div>");
    }
}

// function send_code(transport, method) {
//     if (!code_send) {
//         if (document.getElementById('uid').innerHTML != '') {
//             code_send = true;
//             last_transport = transport;
//             var url = document.getElementById('urlApi').innerHTML + '/users/' + document.getElementById('uid').innerHTML + '/methods/' + method + '/transports/' + transport + '/' + document.getElementById('userHash').innerHTML;
//             request({
//                 method: 'POST',
//                 url: url
//             }, function(response) {
//                 if (response.code == "Ok") {
//                     success_message('strings.success.transport' + transport);
//                     hide_methods();
//                 } else {
//                     errors_message('strings.error.message' + response.message);
//                 }
//                 code_send = false;
//             });
//         } else errors_message('strings.error.login_needed');
//     } else {
//         errors_message('strings.error.transport_wait' + ' ' + last_transport);
//     }
// };

function send_code(transport, method) {
    $('#list-methods').append('<input type="hidden" name="method" value="' + method + '" />');
    $('#list-methods').append('<input type="hidden" name="transport" value="' + transport + '" />');
    submit();
}