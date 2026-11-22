# Run:  .\run_all.ps1 > results-windows.md
# Output is captured through cmd so the bytes are not re-encoded by PowerShell.
$ErrorActionPreference = 'Stop'
New-Item -ItemType Directory -Force build | Out-Null
javac --release 17 -Xlint:all -Werror -d build (Get-ChildItem src/*.java, tests/*.java).FullName *> java-build.log
if ($LASTEXITCODE -ne 0) { Write-Output "Java: compile FAILED (see java-build.log)"; exit 1 }
Write-Output "| Check | Result |"
Write-Output "| --- | --- |"
$failed = 0
function Same($name, $cmd, $expected, $wantExit) {
    cmd /c "$cmd > build\out.txt 2> build\err.txt"
    $code = $LASTEXITCODE
    $got = [IO.File]::ReadAllText("$PWD\build\out.txt").Replace("`r", "")
    $want = [IO.File]::ReadAllText("$PWD\tests\expected\$expected").Replace("`r", "")
    if ($got -eq $want -and $code -eq $wantExit) { Write-Output "| $name | pass |" }
    else { Write-Output "| $name | FAIL (exit $code) |"; $script:failed++ }
}
Same "PatternsTest (all patterns, shape rules, Pascal)" "java -ea -cp build PatternsTest tests\expected" "PatternsTest.txt" 0
Same "PatternsDemo list" "java -cp build PatternsDemo list" "list.txt" 0
Same "PatternsDemo pascal 7" "java -cp build PatternsDemo pascal 7" "pascal-7.txt" 0
Same "Pyramid with 5" "java -cp build Pyramid < tests\input-5.txt" "pyramid-5.txt" 0
Same "Pyramid rejects 'four'" "java -cp build Pyramid < tests\input-four.txt" "pyramid-rejected.txt" 1
cmd /c "java -cp build PatternsDemo pyramid 51 > nul 2> nul"
if ($LASTEXITCODE -eq 2) { Write-Output "| PatternsDemo rejects 51 rows | pass |" }
else { Write-Output "| PatternsDemo rejects 51 rows | FAIL |"; $failed++ }
Write-Output ""
Write-Output ("Java version: " + ((cmd /c "java -version 2>&1") | Select-Object -First 1))
if ($failed -gt 0) { Write-Output "Java: $failed check(s) FAILED"; exit 1 }
Write-Output "Java: all checks passed"
