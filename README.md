# Differential Check for Maven

A framework and maven plugin for applying differential checks or rules on git based projects.
A differential check determines violations present in the diff between a given source and target branch.
Only edits are checked for violations, i.e. lines that were added or modified but not deleted.

This can be useful to enforce code quality tools as part of git pull-request workflow. This tool encourages 
developers to improve new and existing design, one pull-request/branch at a time. Combined with the git-ratchet tool, which takes more of a global approach for quality improvement, existing 
codebase can be improved iteratively.

## Running Differential Check

Checks for violation using the `check` goal:

    mvn dfcheck:dfcheck
    
The target branch is by default the `master` branch, but can be overridden as follows

    mvn dfcheck:dfcheck -Ddfcheck.target=integration-branch
    
Violations are printed in the following format:

    FileA:
        [LineNumber1] ViolationDescription1 [PluginName:ViolationSource1]
        [LineNumber2] ViolationDescription2 [PluginName:ViolationSource2]
        
    FileB:
        [LineNumber1] ViolationDescription1 [PluginName:ViolationSource1]
    ...

## Show Effective Diff

In order to see the effective diff that will be checked for violations, use the `diff` goal
    
    mvn dfcheck:diff

The format of the output is:

    FileA:[[lineNumber-range1], [lineNumber-range2] ..]
    FileB:[[lineNumber-range1], [lineNumber-range2] ..]
    ...
    
    
## License

Copyright 2015 Raz Shahriar 

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions
and limitations under the License.