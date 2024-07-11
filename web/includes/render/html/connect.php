<?php

function isSteamOverlay() {
    return strpos($_SERVER['HTTP_USER_AGENT'], "Valve Steam GameOverlay") !== false;
}

function htmlHeader($data) {
    if (!isset($data['Error'])) {?>
    <script>
(()=>{
    const copyTextToClipboard = (text) => {
        if (!!navigator.clipboard) {
            navigator.clipboard.writeText(text).then(()=>{
                alerty.toasts('Command copied to clipboard')
            }, (err)=>{
                alerty.toasts('Could not copy command')
            });
        } else {
            const textArea = document.createElement("textarea")
            textArea.value = text
            textArea.style.top = "0"
            textArea.style.left = "0"
            textArea.style.position = "fixed"
            document.body.appendChild(textArea)
            textArea.focus()
            textArea.select()

            try {
                if (document.execCommand('copy')) alerty.toasts('Command copied to clipboard')
                else alerty.toast('Could not copy command')
            } catch (err) {
                alerty.toasts('Oops, unable to copy')
            }

            document.body.removeChild(textArea)
        }
    }
    document.addEventListener('DOMContentLoaded', ()=>{
        Array.from(document.querySelectorAll('main code')).forEach((x)=>x.addEventListener('click', (e)=>copyTextToClipboard(e.target.innerText)))
        window.setTimeout(()=>window.location.href="steam://connect/<?=$data['Address']?>", 100)
    })
})()
    </script><?php
    }
}

function htmlRender($data) {
    if (isset($data['Error'])) {
        ?><h1>Error</h1><p><?=htmlspecialchars($data['Error'])?></p><?php
    } else if (isSteamOverlay()) {
        ?><h1>Match found!</h1>
        <p>You are on your way to '<i><?=htmlspecialchars($data['Name'])?></i>' running <?=htmlspecialchars($data['Map'])?>.</p>
        <p>Run this command in your console:<br><code style="text-align: center; display: block;">connect <?=htmlspecialchars($data['Address'])?></code></p>
        <p>Sorry, but the ingame overlay <i>can not</i> automatically connect you to a server.</p><?php
    } else {
        ?><h1>Connecting...</h1>
        <p>You are on your way to '<i><?=htmlspecialchars($data['Name'])?></i>' running <?=htmlspecialchars($data['Map'])?>.</p>
        <p>If nothing happens for more than a few seconds, you can try to click <a href="steam://connect/<?=$data['Address']?>">here</a> or type <code>connect <?=htmlspecialchars($data['Address'])?></code> into your games console.</p>
        <p>For the best experience we recommend you always allow opening steam:// links. Once you are connected, you can close this window/tab.</p><?php
    }
}