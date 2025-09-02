#!/bin/bash

# Test Step 1 - Bipartite Matching Reduction
echo "=== Testing Step 1: Bipartite Matching Reduction ==="

# Gå till projektets root (från scripts/ till root)
cd "$(dirname "$0")/.."
PROJECT_ROOT=$(pwd)

echo "Project root: $PROJECT_ROOT"

echo "=== Kompilerar projekt ==="
mvn compile

if [ $? -ne 0 ]; then
    echo "❌ Kompilering misslyckades!"
    exit 1
fi

# Gå till target/classes (combine kräver detta)
cd target/classes

echo "=== Testar med matchningstest ==="
$PROJECT_ROOT/scripts/combine java com.rimfrost.BipRed \; $PROJECT_ROOT/scripts/maxflow < $PROJECT_ROOT/testfall/matchningstest.indata > $PROJECT_ROOT/results_matchningstest

echo "=== Jämför resultat för matchningstest ==="
cd $PROJECT_ROOT
diff results_matchningstest testfall/matchningstest.utdata

if [ $? -eq 0 ]; then
    echo "✅ matchningstest: PASS"
    echo "🎉 Din kod är korrekt för steg 1!"
    echo "Du kan nu skicka in till Kattis: kth.adk.reducetoflow"
else
    echo "❌ matchningstest: FAIL"
    echo "--- Förväntat ---"
    cat testfall/matchningstest.utdata
    echo ""
    echo "--- Ditt resultat ---"
    cat results_matchningstest
    echo ""
    echo "Trolig orsak: Extra tom rad - prova ta bort io.close()"
fi

echo ""
echo "=== Test komplett för steg 1 ==="
