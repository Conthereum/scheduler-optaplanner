<?xml version="1.0" encoding="UTF-8"?>
<plannerBenchmark>
  <benchmarkDirectory>local/data/transactionbalancing/template</benchmarkDirectory>
  <parallelBenchmarkCount>AUTO</parallelBenchmarkCount>

  <inheritedSolverBenchmark>
    <problemBenchmarks>
      <solutionFileIOClass>org.optaplanner.examples.transactionbalancing.persistence.TransactionBalanceSolutionFileIO</solutionFileIOClass>
      <!--<inputSolutionFile>data/transactionbalancing/unsolved/2computers-6processes.json</inputSolutionFile>-->
      <!--<inputSolutionFile>data/transactionbalancing/unsolved/3computers-9processes.json</inputSolutionFile>-->
      <!--<inputSolutionFile>data/transactionbalancing/unsolved/4computers-12processes.json</inputSolutionFile>-->
      <inputSolutionFile>data/transactionbalancing/unsolved/100computers-300processes.json</inputSolutionFile>
      <inputSolutionFile>data/transactionbalancing/unsolved/200computers-600processes.json</inputSolutionFile>
      <inputSolutionFile>data/transactionbalancing/unsolved/400computers-1200processes.json</inputSolutionFile>
      <inputSolutionFile>data/transactionbalancing/unsolved/800computers-2400processes.json</inputSolutionFile>
    </problemBenchmarks>

    <solver>
      <solutionClass>org.optaplanner.examples.transactionbalancing.domain.TransactionBalance</solutionClass>
      <entityClass>org.optaplanner.examples.transactionbalancing.domain.TransactionProcess</entityClass>
      <scoreDirectorFactory>
        <constraintProviderClass>org.optaplanner.examples.transactionbalancing.score.TransactionBalancingConstraintProvider</constraintProviderClass>
        <initializingScoreTrend>ONLY_DOWN/ONLY_DOWN</initializingScoreTrend>
      </scoreDirectorFactory>
      <termination>
        <minutesSpentLimit>5</minutesSpentLimit>
      </termination>
      <constructionHeuristic>
        <constructionHeuristicType>FIRST_FIT_DECREASING</constructionHeuristicType>
      </constructionHeuristic>
    </solver>
  </inheritedSolverBenchmark>

<#list [5, 7, 11, 13] as entityTabuSize>
<#list [500, 1000, 2000] as acceptedCountLimit>
  <solverBenchmark>
    <name>entityTabuSize ${entityTabuSize} acceptedCountLimit ${acceptedCountLimit}</name>
    <solver>
      <localSearch>
        <unionMoveSelector>
          <changeMoveSelector/>
          <swapMoveSelector/>
        </unionMoveSelector>
        <acceptor>
          <entityTabuSize>${entityTabuSize}</entityTabuSize>
        </acceptor>
        <forager>
          <acceptedCountLimit>${acceptedCountLimit}</acceptedCountLimit>
        </forager>
      </localSearch>
    </solver>
  </solverBenchmark>
</#list>
</#list>
</plannerBenchmark>
