# Run:  .\run_all.ps1
$ErrorActionPreference = 'Stop'
New-Item -ItemType Directory -Force build | Out-Null
javac --release 17 -Xlint:all -Werror -d build (Get-ChildItem src/*.java, tests/*.java).FullName *> java-build.log
if ($LASTEXITCODE -ne 0) { Write-Output "Java: compile FAILED (see java-build.log)"; exit 1 }
java -ea -cp build DigestTest tests/vectors
if ($LASTEXITCODE -ne 0) { Write-Output "Java: vectors FAILED"; exit 1 }
Write-Output "Java: compiled and vectors passed"
