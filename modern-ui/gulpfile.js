const gulp        = require('gulp');
const uglify      = require('gulp-uglify');
const concat      = require('gulp-concat');
//const sass        = require('gulp-sass');
const minify      = require('gulp-minify-css');
const jshint      = require('gulp-jshint');
const cached      = require('gulp-cached');
const remember    = require('gulp-remember');
const del         = require('del');
const browserSync = require('browser-sync').create();
const sourcemaps  = require('gulp-sourcemaps');
const newer       = require('gulp-newer');
const ts          = require('gulp-typescript');
const tsProject   = ts.createProject('tsconfig.json', {typescript: require('typescript')});
const KarmaServer = require('karma').Server;


gulp.task('clean', () => {
    return del('target/**/*');
})

gulp.task('lint', () => {
    return gulp.src('src/main/scripts/**/*.ts')
               .pipe(jshint());
});

gulp.task('scripts', () => {
    return gulp.src('src/main/scripts/**/*.ts')
               .pipe(sourcemaps.init())
               .pipe(cached('scripts'))
               .pipe(newer('target/scripts/concat.js'))
               .pipe(ts(tsProject))
               .pipe(uglify())
               .pipe(remember('scripts'))
               .pipe(concat('concat.js'))
               .pipe(sourcemaps.write('.'))
               .pipe(gulp.dest('target/scripts'))
               .pipe(browserSync.stream());
});

gulp.task('static', () => {
    return gulp.src('src/main/resources/**/*')
               .pipe(newer('target'))
               .pipe(gulp.dest('target'))
               .pipe(browserSync.stream());
});

gulp.task('styles', () => {
    return gulp.src('src/main/styles/**/*.css')
               .pipe(sourcemaps.init())
               .pipe(newer('target/styles'))
//               .pipe(sass())
               .pipe(minify())
               .pipe(sourcemaps.write('.'))
               .pipe(gulp.dest('target/styles'))
               .pipe(browserSync.stream());
});


gulp.task('libs', () => {
    return gulp.src([
                  'bundle/angular2.dev.js',
                  'node_modules/traceur/bin/traceur-runtime.js',
                  'node_modules/es6-module-loader/dist/es6-module-loader.js',
                  'node_modules/systemjs/dist/system.js'
               ])
               .pipe(gulp.dest('target/scripts'));
});


gulp.task('test-build', () => {
	return gulp.src(['src/test/**/**.ts'])
		       .pipe(sourcemaps.init())
               .pipe(ts(tsProject))
		       .pipe(sourcemaps.write('.'))
               .pipe(gulp.dest('target/test'));
});

gulp.task('test-run', (done) => {
	new KarmaServer({
		configFile: __dirname + '/karma.conf.js',
		singleRun: true
	}, done).start();
});

gulp.task('test', gulp.series('test-build', 'test-run'));

gulp.task('default', gulp.series(
        'clean',
        gulp.parallel(
            'styles',
            'static',
            gulp.series('lint', 'scripts')
        ),
        'test'
    )
);

gulp.task('server', () => {
        browserSync.init({
            server: {
                baseDir: ["./","target"],
                index: "index.html"
            }
        });

        gulp.watch('src/main/scripts/**/*.ts', gulp.parallel('scripts'));
        gulp.watch('src/main/styles/**/*.css', gulp.parallel('styles'));
        gulp.watch("src/main/resources/**/*", gulp.parallel('static'));
    }
);

gulp.task('run', gulp.series(
    'default',
    'server'
));