module.exports = function (grunt) {
    grunt.initConfig({
        project: grunt.file.readJSON('package.json'),
        dir: {
            "source_ts": "main",
            "target": "target/main"
        },
        ts: {
            compile: {
                src: "<%= dir.source_ts %>/**/*.ts",
                out: "<%= dir.target %>/<%= project.name %>.js",
                options: {
                    target: "es5",
                    declaration: false
                }
            },
            compile_tsx: {
                src: "<%= dir.source_ts %>/**/*.tsx",
                out: "<%= dir.target %>/<%= project.name %>.jsx",
                options: {
                    target: "es5",
                    declaration: false,
                    jsx: "react"
                }
            }
        }
    });
    grunt.loadNpmTasks("grunt-ts");
    grunt.registerTask("default", ['ts:compile','ts:compile_tsx']);
};
