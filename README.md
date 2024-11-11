# Projeto Android - Integração de Autenticação e API REST

Este projeto consiste na criação de um aplicativo Android com tela de login utilizando Firebase Authentication e integração com uma API REST para exibição e manipulação de dados de carros.

## Requisitos

**IDE**: Android Studio  
**Bibliotecas principais**: Firebase Authentication, Retrofit (para requisições REST), Glide (para carregamento de imagens)

## Funcionalidades Implementadas

1. **Tela de Login com Firebase**
    - O aplicativo implementa uma tela de login utilizando Firebase Authentication com autenticação via número de telefone.
    - A configuração do Firebase permite o login de teste usando o número de telefone `+55 11 91234-5678`, com o código de verificação `101010`.

2. **Opção de Logout**
    - Adicionei uma opção de logout visível no menu principal, permitindo que o usuário saia da conta autenticada a qualquer momento.

3. **Integração com API REST (/car)**
    - O aplicativo consome uma API REST disponível no repositório [FTPR-Car-Api-Node-Express](https://github.com/vagnnermartins/FTPR-Car-Api-Node-Express), que fornece dados dos carros.
    - A integração foi implementada utilizando Retrofit para realizar requisições GET e POST, e os dados dos carros são exibidos na interface do usuário.
    - **Estrutura JSON**: A estrutura dos dados de cada carro segue o modelo JSON especificado:

      ```json
      {
        "imageUrl": "https://image",
        "year": "2020/2020",
        "name": "Gaspar",
        "licence": "ABC-1234",
        "place": {
          "lat": 0,
          "long": 0
        }
      }
      ```

4. **Exibição de Imagens**
    - Devido à limitação do Firebase Storage, que expirou o período de avaliação gratuita, o requisito de utilizar o Firebase Storage para armazenar imagens não foi implementado.
    - Em vez disso, adicionei um campo de entrada (EditText) onde o usuário pode inserir a URL de uma imagem da web.
    - Caso o usuário insira uma URL inválida ou deixe o campo em branco, o aplicativo utiliza uma URL padrão que aponta para uma imagem genérica de carro na web.

5. **RecyclerView para Listagem e Edição de Carros**
    - A Main Activity contém uma RecyclerView que exibe a lista de carros recuperados da API.
    - Cada item da lista exibe as informações do carro, incluindo nome, ano, licença, e imagem.
    - O usuário pode clicar em um item da lista para editar ou excluir o carro selecionado.

6. **Botão para Adicionar Novo Carro**
    - Na parte inferior da Main Activity, há um botão "+" que direciona o usuário para a tela de criação de um novo registro de carro.
    - Nesta tela, o usuário pode inserir os detalhes do carro, incluindo nome, ano, licença, e URL da imagem.

7. **Obtenção de Localização e Permissões**
    - A estrutura JSON dos carros inclui os campos `lat` e `long`, permitindo a associação de uma localização para cada carro.
    - O aplicativo solicita permissões de localização do usuário, necessárias para exibir as coordenadas de cada carro e associar localizações aos registros.

## Configuração do Projeto

1. **Firebase**
    - O aplicativo está configurado para usar Firebase Authentication.
    - É necessário incluir o arquivo `google-services.json` no projeto para habilitar a autenticação via Firebase.

2. **Principais dependências Gradle**
    - Firebase Authentication: para a funcionalidade de login.
    - Retrofit: para a comunicação com a API REST.
    - Glide: para o carregamento de imagens a partir das URLs fornecidas pelo usuário ou padrão.

## Instruções para Uso

1. **Login**
    - Ao abrir o aplicativo, o usuário verá a tela de login.
    - Para fazer login, use o número de telefone `+55 11 91234-5678` com o código de verificação `101010`.

2. **Gerenciamento de Carros**
    - Após o login, o usuário pode ver uma lista de carros consumida da API.
    - Cada carro exibe o nome, ano, placa, e uma imagem (caso a URL seja válida).
    - É possível inserir uma URL de imagem de carro. Se a URL não for válida, a imagem padrão será exibida.
    - O usuário pode clicar em qualquer carro na lista para editá-lo ou excluí-lo.

3. **Adicionar Novo Carro**
    - O usuário pode clicar no botão "+" no rodapé da tela principal para adicionar um novo carro.
    - Na tela de criação, o usuário pode inserir nome, ano, licença, e a URL da imagem do carro.

4. **Logout**
    - O usuário pode deslogar a qualquer momento clicando na opção de logout disponível no menu principal.

---

## OBS:

- **Limitação no Armazenamento de Imagens**: Devido à expiração do free trial do Firebase Storage, não foi implementado o armazenamento de imagens no Firebase. Em vez disso, o aplicativo permite a entrada de URLs de imagens de fontes externas, utilizando uma URL padrão para casos de URLs inválidas.