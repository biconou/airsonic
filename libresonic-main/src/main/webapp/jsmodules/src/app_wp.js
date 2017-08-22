import 'jquery';
import {secretButton, secretParagraph} from './dom-loader_wp';

// This variable is private to current package
var showSecret = false;

// Everything here is executed at the begining
//alert("start of app_wp.js execution");
console.log("Init of module app_wp")
model.name="The name";
console.dir(model);
secretButton.addEventListener('click', toggleSecretState);
updateSecretParagraph();


// Private function definitions
function toggleSecretState() {
    showSecret = !showSecret;
    updateSecretParagraph();
    updateSecretButton()
}

function updateSecretButton() {
    if (showSecret) {
        secretButton.textContent = 'Hide the Secret';
    } else {
        secretButton.textContent = 'Show the Secret';
    }
}

function updateSecretParagraph() {
    if (showSecret) {
        secretParagraph.style.display = 'block';
    } else {
        secretParagraph.style.display = 'none';
    }
}
