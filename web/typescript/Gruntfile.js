module.exports = function (grunt) {
    grunt.initConfig({
        project: grunt.file.readJSON('package.json'),
        dir: {
            "source_ts": "main",
            "source_test_ts": "test",
            "target": "target/main",
            "target_test_js": "target/test",
            "target_report": "target/report"
        },
        ts: {
            compile: {
                src: "<%= dir.source_ts %>/**/*.ts",
                out: "<%= dir.target %>/<%= project.name %>.js",
//                files: [{ src: ["<%= dir.source_ts %>/**/*.ts"], dest: '<%= dir.target %>/<%= project.name %>.js' }],
                options: {
                    target: "es5",
                    declaration: false,
                    module: "commonjs",
                    verbose: true
                }
            },
            compile_tsx: {
                src: "<%= dir.source_ts %>/**/*.tsx",
                out: "<%= dir.target %>/<%= project.name %>.jsx",
//                files: [{ src: ["<%= dir.source_ts %>/**/*.ts"], dest: '<%= dir.target %>/<%= project.name %>.js' }],
                options: {
                    target: "es5",
                    declaration: false,
                    jsx: "react",
                    module: "commonjs",
                    verbose: true
                }
                //},
                //compile_test: {
                //    src: ["<%= dir.source_test_ts %>/**/*.ts"],
                //    dest: '<%= dir.target_test_js %>',
                //    options: {
                //        target: "es5",
                //        declaration: false,
                //        jsx: "react",
                //        module: "commonjs"
                //    }
            }
//        },
//        jasmine: {
//            run: {
//                src: ['<%= dir.target_js %>/**/*.js'],
//                options: {
//                    specs: '<%= dir.target_test_js %>/**/*Spec.js'
//                }
//            }
//        },
//        concat: {
//            options: {
//                separator: ';'
//            },
//            dist: {
//                src: ["<%= dir.source_ts %>/**/*.js"],
//                dest: '<%= dir.target %>/<%= project.name %>.js'
//            }
        }
    });
    grunt.loadNpmTasks("grunt-ts");
//    grunt.loadNpmTasks('grunt-contrib-jasmine');
//    grunt.loadNpmTasks("grunt-contrib-concat");
    grunt.registerTask("default", ['ts:compile','ts:compile_tsx'/*,'ts:compile_test','jasmine','concat'*/]);
};
