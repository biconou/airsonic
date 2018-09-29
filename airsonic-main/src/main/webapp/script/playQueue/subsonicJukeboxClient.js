
function subsonicJukeboxStart() {
    console.log("subsonicJukeboxStart");
    $.get( "rest/jukeboxControl.view?v=1.15.0&c=airsonic&f=json&action=start&player=" + javaJukeboxPlayerModel.player, function( data ) {
        console.log('+++ play')
      });
}