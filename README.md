# Differential Check for Maven

A framework and maven plugin for applying differential checks or rules on projects using git.
A differential check determines violations present in the diff between a given source and target branch.
Only edits are checked for violations, i.e. lines that were added or modified but not deleted.

This can be useful to enforce code quality tools as part of git pull-request workflow. This tool encourages 
developers to improve new and existing design, one pull-request/branch at a time. Combined with the git-ratchet tool, which takes more of a global approach for quality improvement, existing 
codebase can be improved iteratively.


## Usage

DfCheck maven plugin can be added to the project's `pom.xml` as follows:
                
                <build>
                    <plugins>
                        ...
                        <plugin>
                            <groupId>com.atlassian.dfcheck</groupId>
                            <artifactId>dfcheck-maven-plugin</artifactId>
                            <version>1.0</version>
                            <executions>
                                <execution>
                                    <id>diff-check</id>
                                    <goals>
                                        <goal>dfcheck</goal>
                                    </goals>
                                </execution>
                            </executions>
                        </plugin>
                        ...
                    </plugins>
                </build>
                
By default DfCheck binds itself to the `VERIFY` Lifecycle Phase of maven. It can be customised via 
the `<execution>` element.
    
## Configuration

The plugin can be configured using the `<configuration>` element inside the plugin declaration. These
can also be passed as command line parameters e.g. `-Ddfcheck.skip=true`

- `failOnViolation` - Fail the build if there are violations, `true|false` Default: `true`
- `readUncommitted` - Read uncommitted changes (by default), set to `false` if only committed changes must be tracked `true|false`, Default: `true`
- `mode` - How to calculate the effective diff, can be: `add|mod|edit`, Default: `edit`
    - `add` mode: only lines that were added will be checked.
    - `mod` mode: only lines that were modified will be checked.
    - `edit` mode (default): combination of `add` and `mod`, both additions and modifications are included

- `consoleOutput` - whether violations should be printed in console, `true|false` Default: `true`  
- `skip` - Skip execution of the plugin. `true|false`, Default: `false`

### Checkstyle

- `checkstyleEnabled` - whether checkstyle can raise violations `true|false` Default: `true`
- `checkstyleSeverity` - the severity that is considered a violation `warn|error` Default: `error`
- `checkstyleReport` - the directory where checkstyle report is generated. Default: `${project.build.directory}/checkstyle-result.xml`

### PMD

- `pmdEnabled` - whether PMD can raise violations `true|false` Default: `false`
- `pmdSeverity` - the severity that is considered a violation `warn|error` Default: `error`
- `pmdReport` - the directory where PMD report is generated. 

### FindBugs
- `findBugsEnabled` - whether findbugs can raise violations `true|false` Default: `false`
- `findBugsSeverity` - the severity that is considered a violation `warn|error` Default: `error`
- `findBugsReport` - the directory where findbugs report is generated. 


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