//Step-1
/*
Fetch the already saved notes of website, in the method saveNotes() we already saved the notes in browser 
local storage with key savedNotes

*/

//*************OLD METHOD FOR STEP 1 Exceution*******
// document.addEventListener('DOMContentLoaded', () => {
//     chrome.storage.local.get(['savedNotes'], function(result) {
//        if (result.researchNotes) {
//         document.getElementById('notes').value = result.researchNotes;
//        } 
//     });

//     document.getElementById('summarizeBtn').addEventListener('click', summarizeText);
//     document.getElementById('saveNotesBtn').addEventListener('click', saveNotes);
// });


//*********NEW METHOD FOR STEP 1 EXECUTION */
document.addEventListener('DOMContentLoaded', async () => {
    const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
    const urlKey = 'notes_' + new URL(tab.url).origin;
    chrome.storage.local.get([urlKey], function(result) {
        if (result[urlKey]) {
            document.getElementById('notes').value = result[urlKey];
        }
    });

    document.getElementById('summarizeBtn').addEventListener('click', summarizeText);
    document.getElementById('saveNotesBtn').addEventListener('click', saveNotes);
});


async function summarizeText() {
    try {
         loader.style.display = 'inline-block';
        const [tab] = await chrome.tabs.query({ active:true, currentWindow: true});
        const [{ result }] = await chrome.scripting.executeScript({
            target: {tabId: tab.id},
            function: () => window.getSelection().toString()
        });

        if (!result) {
            showResult('Please select some text first');
            loader.style.display = 'none';  // Hide loader
            return;
        }

        const response = await fetch('http://localhost:8080/api/research/process-request', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ content: result, operation: 'summarize'})
        });

        if (!response.ok) {
            throw new Error(`API Error: ${response.status}`);
        }

        const text = await response.text();
        showResult(text.replace(/\n/g,'<br>'));

    } catch (error) {
        showResult('Error: ' + error.message);
    }finally {
    loader.style.display = 'none';  // Always hide loader at the end
  }
}

//*****OLD METHOD OF SAVING NOTES */
// async function saveNotes() {
//     const notes = document.getElementById('notes').value;
//     chrome.storage.local.set({ 'savedNotes': notes}, function() {
//         alert('Notes saved successfully');
//     });
// }

//*******NEW METHOD OF SAVING NOTES******** */
async function saveNotes() {
    const notes = document.getElementById('notes').value;
    const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });//obtaining the url link
    const urlKey = 'notes_' + new URL(tab.url).origin; // now the notes_(urlLink) will be our new key
    chrome.storage.local.set({ [urlKey]: notes }, function() {
        alert('Notes saved successfully');
    });
}


function showResult(content) {
    document.getElementById('results').innerHTML = `<div class="result-item"><div class="result-content">${content}</div></div>`;
}