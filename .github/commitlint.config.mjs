// Conventional commits (DEVELOPMENT.md): feat, fix, docs, style, refactor,
// perf, test, build, ci, chore, revert. Subject ≤72 chars, imperative mood.
export default {
    extends: ['@commitlint/config-conventional'],
    rules: {
        'subject-case': [0],
        'header-max-length': [2, 'always', 100],
    },
};
