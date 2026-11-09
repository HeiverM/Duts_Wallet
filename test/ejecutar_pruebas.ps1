$ErrorActionPreference = "Continue"
$javac = "C:\Users\Heiver\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.12.8-hotspot\bin\javac.exe"
$java = "C:\Users\Heiver\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.12.8-hotspot\bin\java.exe"
$proyecto = (Get-Location).Path
$run = "C:\Users\Heiver\AppData\Local\Temp\opencode\duts_run8"

if (Test-Path $run) { Remove-Item -Recurse -Force $run }
New-Item -ItemType Directory -Path $run -Force | Out-Null
Copy-Item -Recurse -Force "$proyecto\recursos" "$run\recursos"

$archivos = @((Get-ChildItem -Recurse "$proyecto\src" -Filter *.java).FullName) + @((Get-ChildItem "$proyecto\test" -Filter *.java).FullName)
$salida = & $javac --release 8 -encoding UTF-8 -d "$run\out" $archivos 2>&1
$errores = $salida | Where-Object { "$_" -match "error" }
if ($errores) { Write-Host "COMPILACION: ERRORES"; $errores; exit 1 }
$clases = (Get-ChildItem "$run\out" -Recurse -Filter *.class | Measure-Object).Count
Write-Host "COMPILACION: OK ($clases clases)"

$lista = Select-String -Path "$proyecto\src\*\*.java" -Pattern '->', '::', '\.stream\(', 'enum ', 'Comparator', 'Collections\.sort', 'Thread', 'Timer', 'SwingWorker', 'System\.out', 'java\.nio', 'GroupLayout', 'new \w+\(\) *\{'
if ($lista) { Write-Host "LISTA NEGRA: COINCIDENCIAS"; $lista } else { Write-Host "LISTA NEGRA: 0 coincidencias" }

Push-Location $run
foreach ($prueba in $args) {
    Write-Host "--- $prueba ---"
    & $java -cp out $prueba.Split(" ") 2>&1 | Select-String -Pattern "FALLA|Pruebas|RESULTADO|Exception|Error"
}
Pop-Location
