# Experiments with ollama native install

See https://github.com/ollama/ollama

## simple interactive usage

```
ollama run codellama
```

## Use the rest api

```
curl localhost:11434/api/generate -d '{ "model": "codellama", "prompt": "what is AtomicInteger?" }'

```


